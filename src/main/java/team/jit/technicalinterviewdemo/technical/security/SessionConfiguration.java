package team.jit.technicalinterviewdemo.technical.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.config.SessionRepositoryCustomizer;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@Configuration
@EnableJdbcHttpSession
public class SessionConfiguration {

    /**
     * Concurrent first requests on a fresh session all see a missing attribute and race the
     * same {@code SPRING_SESSION_ATTRIBUTES} insert at session commit. The PostgreSQL upsert
     * keeps the losing writers from failing with a duplicate-key error.
     */
    private static final String CREATE_SESSION_ATTRIBUTE_QUERY = """
        INSERT INTO %TABLE_NAME%_ATTRIBUTES (SESSION_PRIMARY_ID, ATTRIBUTE_NAME, ATTRIBUTE_BYTES)
        VALUES (?, ?, ?)
        ON CONFLICT (SESSION_PRIMARY_ID, ATTRIBUTE_NAME)
        DO UPDATE SET ATTRIBUTE_BYTES = EXCLUDED.ATTRIBUTE_BYTES
        """;

    @Bean
    SessionRepositoryCustomizer<JdbcIndexedSessionRepository> sessionAttributeUpsertCustomizer() {
        return sessionRepository -> sessionRepository.setCreateSessionAttributeQuery(CREATE_SESSION_ATTRIBUTE_QUERY);
    }
}
