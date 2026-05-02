package team.jit.technicalinterviewdemo.external;

import java.util.Map;
import javax.sql.DataSource;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import team.jit.technicalinterviewdemo.technical.security.SessionConfiguration;

final class ExternalSessionSupport implements AutoCloseable {

    private final AnnotationConfigApplicationContext context;
    private final JdbcTemplate jdbcTemplate;
    private final SessionRepository<Session> sessionRepository;

    private ExternalSessionSupport(
            AnnotationConfigApplicationContext context,
            JdbcTemplate jdbcTemplate,
            SessionRepository<Session> sessionRepository
    ) {
        this.context = context;
        this.jdbcTemplate = jdbcTemplate;
        this.sessionRepository = sessionRepository;
    }

    static ExternalSessionSupport create() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                requiredSystemProperty("external.jdbc.url"),
                requiredSystemProperty("external.jdbc.user"),
                requiredSystemProperty("external.jdbc.password")
        );
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBean(DataSource.class, () -> dataSource);
        context.registerBean(JdbcTemplate.class, () -> new JdbcTemplate(dataSource));
        context.registerBean(DataSourceTransactionManager.class, () -> new DataSourceTransactionManager(dataSource));
        context.register(SessionConfiguration.class);
        context.refresh();

        JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
        @SuppressWarnings({"rawtypes", "unchecked"})
        SessionRepository<Session> sessionRepository = (SessionRepository) context.getBean(SessionRepository.class);
        return new ExternalSessionSupport(context, jdbcTemplate, sessionRepository);
    }

    String createAuthenticatedSession(String login) {
        DefaultOAuth2User oauth2User = new DefaultOAuth2User(
                AuthorityUtils.createAuthorityList("ROLE_USER"),
                Map.of(
                        "login", login,
                        "name", login + " display",
                        "email", login + "@example.test"
                ),
                "login"
        );
        SecurityContext securityContext = new SecurityContextImpl(
                new OAuth2AuthenticationToken(oauth2User, oauth2User.getAuthorities(), "github")
        );
        Session session = sessionRepository.createSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
        sessionRepository.save(session);
        return session.getId();
    }

    int sessionRowCount(String sessionId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM SPRING_SESSION WHERE SESSION_ID = ?",
                Integer.class,
                sessionId
        );
        return count == null ? 0 : count;
    }

    int sessionAttributeCount(String sessionId) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM SPRING_SESSION_ATTRIBUTES attributes
                JOIN SPRING_SESSION sessions ON sessions.PRIMARY_ID = attributes.SESSION_PRIMARY_ID
                WHERE sessions.SESSION_ID = ?
                """,
                Integer.class,
                sessionId
        );
        return count == null ? 0 : count;
    }

    boolean hasStoredSecurityContext(String sessionId) {
        Session storedSession = sessionRepository.findById(sessionId);
        if (storedSession == null) {
            return false;
        }
        Object storedSecurityContext =
                storedSession.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        return storedSecurityContext instanceof SecurityContext securityContext
                && securityContext.getAuthentication() instanceof OAuth2AuthenticationToken
                && securityContext.getAuthentication().isAuthenticated();
    }

    private static String requiredSystemProperty(String name) {
        String value = System.getProperty(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing system property: " + name);
        }
        return value.trim();
    }

    @Override
    public void close() {
        context.close();
    }
}
