package team.jit.technicalinterviewdemo.technical.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import team.jit.technicalinterviewdemo.technical.testing.IntegrationSpringBootTest;

@IntegrationSpringBootTest
class SecurityIntegrationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcIndexedSessionRepository sessionRepository;

    @SuppressWarnings({"rawtypes", "unchecked"})
    private SessionRepository<Session> httpSessionRepository() {
        return (SessionRepository) sessionRepository;
    }

    @BeforeEach
    void clearSessions() {
        jdbcTemplate.update("DELETE FROM SPRING_SESSION_ATTRIBUTES");
        jdbcTemplate.update("DELETE FROM SPRING_SESSION");
    }

    @Test
    void jdbcSessionRepositoryStoresOAuth2SecurityContext() {
        DefaultOAuth2User oauth2User = new DefaultOAuth2User(
                AuthorityUtils.createAuthorityList("ROLE_USER"),
                Map.of("login", "demo-user"),
                "login"
        );
        SecurityContext securityContext = new SecurityContextImpl(
                new OAuth2AuthenticationToken(oauth2User, oauth2User.getAuthorities(), "github")
        );
        Session session = httpSessionRepository().createSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
        httpSessionRepository().save(session);

        Integer sessions = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM SPRING_SESSION WHERE SESSION_ID = ?",
                Integer.class,
                session.getId()
        );
        Integer attributes = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM SPRING_SESSION_ATTRIBUTES",
                Integer.class
        );
        Session storedSession = httpSessionRepository().findById(session.getId());

        assertThat(sessions).isEqualTo(1);
        assertThat(attributes).isGreaterThan(0);
        assertThat(storedSession).isNotNull();
        Object storedSecurityContext =
                storedSession.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        assertThat(storedSecurityContext).isNotNull();
    }
}
