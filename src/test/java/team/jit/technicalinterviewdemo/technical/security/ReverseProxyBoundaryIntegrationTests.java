package team.jit.technicalinterviewdemo.technical.security;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;
import team.jit.technicalinterviewdemo.testing.AbstractMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcIntegrationSpringBootTest
@ActiveProfiles(value = {"test", "prod", "oauth"}, inheritProfiles = false)
@TestPropertySource(properties = {"server.servlet.session.cookie.secure=true", "app.security.oauth.providers.github.client-id=test-client-id", "app.security.oauth.providers.github.client-secret=test-client-secret"
})
class ReverseProxyBoundaryIntegrationTests extends AbstractMockMvcIntegrationTest {

    @Test
    void oauthAuthorizationRedirectUsesForwardedHeadersInProd() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/session/oauth2/authorization/github").header("X-Forwarded-Proto", "https").header("X-Forwarded-Host", "demo.example.test")).andExpect(status().is3xxRedirection()).andReturn();

        String location = result.getResponse().getHeader("Location");
        assertThat(location).startsWith("https://github.com/login/oauth/authorize?");
        assertThat(UriComponentsBuilder.fromUriString(location).build(true).getQueryParams().getFirst("redirect_uri")).isEqualTo("https://demo.example.test/api/session/login/oauth2/code/github");
    }

    @Test
    void prodSecureRequestsIncludeStrictTransportSecurityHeader() throws Exception {
        mockMvc.perform(get("/api/session").secure(true)).andExpect(status().isOk()).andExpect(header().string("Strict-Transport-Security", "max-age=31536000 ; includeSubDomains"));
    }
}
