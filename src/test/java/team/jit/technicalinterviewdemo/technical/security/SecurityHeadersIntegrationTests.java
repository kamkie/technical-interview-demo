package team.jit.technicalinterviewdemo.technical.security;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import team.jit.technicalinterviewdemo.testing.AbstractMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcIntegrationSpringBootTest
class SecurityHeadersIntegrationTests extends AbstractMockMvcIntegrationTest {

    @Test
    void securityHeadersApplyToInternalOverviewAndDocsSurfaces() throws Exception {
        assertSecurityHeaders(mockMvc.perform(get("/")).andExpect(status().isOk()));
        assertSecurityHeaders(mockMvc.perform(get("/docs")).andExpect(status().isFound()));
        assertSecurityHeaders(mockMvc.perform(get("/actuator/health/readiness")).andExpect(status().isOk()));
    }

    @Test
    void securityHeadersApplyToApiSessionAndProblemResponses() throws Exception {
        assertSecurityHeaders(mockMvc.perform(get("/api/session")).andExpect(status().isOk()));
        assertSecurityHeaders(mockMvc.perform(get("/api/admin/operator-surface")).andExpect(status().isUnauthorized()));
    }

    private void assertSecurityHeaders(ResultActions resultActions) throws Exception {
        resultActions
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("Referrer-Policy", "no-referrer"))
                .andExpect(header().string("Permissions-Policy", "geolocation=(), microphone=(), camera=()"))
                .andExpect(header().doesNotExist("Strict-Transport-Security"));
    }
}
