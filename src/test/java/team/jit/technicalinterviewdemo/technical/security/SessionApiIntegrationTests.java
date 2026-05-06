package team.jit.technicalinterviewdemo.technical.security;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import team.jit.technicalinterviewdemo.testing.AbstractMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;

@MockMvcIntegrationSpringBootTest
class SessionApiIntegrationTests extends AbstractMockMvcIntegrationTest {

    @Test
    void sessionEndpointReturnsAnonymousStateWhenOauthProfileIsInactive() throws Exception {
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.accountPath").value("/api/account"))
                .andExpect(jsonPath("$.loginProviders.length()").value(0))
                .andExpect(jsonPath("$.loginPath").doesNotExist())
                .andExpect(jsonPath("$.logoutPath").value("/api/session/logout"))
                .andExpect(jsonPath("$.sessionCookie.name").value("technical-interview-demo-session"))
                .andExpect(jsonPath("$.sessionCookie.httpOnly").value(true))
                .andExpect(jsonPath("$.sessionCookie.sameSite").value("lax"))
                .andExpect(jsonPath("$.sessionCookie.secure").value(false))
                .andExpect(jsonPath("$.csrf.enabled").value(true))
                .andExpect(jsonPath("$.csrf.cookieName").value("XSRF-TOKEN"))
                .andExpect(jsonPath("$.csrf.headerName").value("X-XSRF-TOKEN"))
                .andExpect(header().string(
                                HttpHeaders.SET_COOKIE,
                                allOf(
                                        containsString("XSRF-TOKEN="),
                                        containsString("Path=/"),
                                        not(containsString("HttpOnly")))));
    }

    @Test
    void logoutEndpointIsIdempotentWithoutExistingSession() throws Exception {
        mockMvc.perform(post("/api/session/logout"))
                .andExpect(status().isNoContent())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().exists("traceparent"))
                .andExpect(header().stringValues(
                                HttpHeaders.SET_COOKIE,
                                allOf(
                                        hasItem(allOf(
                                                containsString("technical-interview-demo-session="),
                                                containsString("Max-Age=0"),
                                                containsString("HttpOnly"))),
                                        hasItem(allOf(
                                                containsString("XSRF-TOKEN="),
                                                containsString("Max-Age=0"),
                                                not(containsString("HttpOnly")))))))
                .andExpect(content().string(""));
    }
}
