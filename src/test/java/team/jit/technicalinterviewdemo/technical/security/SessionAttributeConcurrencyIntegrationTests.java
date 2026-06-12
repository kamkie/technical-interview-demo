package team.jit.technicalinterviewdemo.technical.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import team.jit.technicalinterviewdemo.testing.IntegrationSpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@IntegrationSpringBootTest
class SessionAttributeConcurrencyIntegrationTests {

    private static final String SYNCED_USER_ATTRIBUTE =
            AuthenticatedUserSynchronizationFilter.class.getName() + ".syncedUser";

    @Autowired
    private JdbcIndexedSessionRepository sessionRepository;

    @Test
    void concurrentFirstRequestsInsertingTheSameNewAttributeBothSave() {
        assertConcurrentAttributeWrites(sessionRepository, "github:reader-user", "github:reader-user");
    }

    @Test
    void concurrentSavesWithDifferentValuesKeepTheLastWrite() {
        assertConcurrentAttributeWrites(sessionRepository, "github:first-user", "github:second-user");
    }

    private static <S extends Session> void assertConcurrentAttributeWrites(
            SessionRepository<S> repository, String firstValue, String lastValue) {
        S session = repository.createSession();
        repository.save(session);
        try {
            // Two request threads load the same fresh session before either commits, so both
            // track the new attribute as an insert when their session commits.
            S firstRequestSession = repository.findById(session.getId());
            S secondRequestSession = repository.findById(session.getId());
            firstRequestSession.setAttribute(SYNCED_USER_ATTRIBUTE, firstValue);
            secondRequestSession.setAttribute(SYNCED_USER_ATTRIBUTE, lastValue);

            repository.save(firstRequestSession);
            assertThatCode(() -> repository.save(secondRequestSession)).doesNotThrowAnyException();

            S reloadedSession = repository.findById(session.getId());
            assertThat((String) reloadedSession.getAttribute(SYNCED_USER_ATTRIBUTE))
                    .isEqualTo(lastValue);
        } finally {
            repository.deleteById(session.getId());
        }
    }
}
