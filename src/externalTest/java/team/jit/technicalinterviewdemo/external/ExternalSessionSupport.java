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

    static boolean isJdbcConfigured() {
        String jdbcUrl = value("external.jdbc.url", "EXTERNAL_JDBC_URL");
        String jdbcUser = value("external.jdbc.user", "EXTERNAL_JDBC_USER");
        String jdbcPassword = value("external.jdbc.password", "EXTERNAL_JDBC_PASSWORD");
        return jdbcUrl != null && jdbcUser != null && jdbcPassword != null;
    }

    static ExternalSessionSupport create() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                requiredConfigurationValue("external.jdbc.url", "EXTERNAL_JDBC_URL"),
                requiredConfigurationValue("external.jdbc.user", "EXTERNAL_JDBC_USER"),
                requiredConfigurationValue("external.jdbc.password", "EXTERNAL_JDBC_PASSWORD")
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

    boolean hasFlywaySchemaHistoryTable() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = 'public' AND table_name = 'flyway_schema_history'
                """,
                Integer.class
        );
        return count != null && count == 1;
    }

    int successfulFlywayMigrationCount() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history WHERE success = true",
                Integer.class
        );
        return count == null ? 0 : count;
    }

    private static String requiredConfigurationValue(String propertyName, String environmentName) {
        String value = value(propertyName, environmentName);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing JDBC configuration. Provide system property "
                            + propertyName
                            + " or environment variable "
                            + environmentName
                            + "."
            );
        }
        return value.trim();
    }

    private static String value(String propertyName, String environmentName) {
        String systemPropertyValue = System.getProperty(propertyName);
        if (systemPropertyValue != null && !systemPropertyValue.isBlank()) {
            return systemPropertyValue.trim();
        }
        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue.trim();
        }
        return null;
    }

    @Override
    public void close() {
        context.close();
    }
}
