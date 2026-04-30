package team.jit.technicalinterviewdemo;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;

import java.util.Map;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public final class SecurityTestSupport {

    private SecurityTestSupport() {
    }

    public static RequestPostProcessor csrfToken() {
        return csrf();
    }

    public static RequestPostProcessor oauthUser() {
        return oauthUser("demo-user");
    }

    public static RequestPostProcessor oauthUser(String login) {
        DefaultOAuth2User oauth2User = new DefaultOAuth2User(
                AuthorityUtils.createAuthorityList("ROLE_USER"),
                Map.of(
                        "login", login,
                        "name", login + " display",
                        "email", login + "@example.test"
                ),
                "login"
        );
        return oauth2Login()
                .clientRegistration(githubClientRegistration())
                .oauth2User(oauth2User);
    }

    public static RequestPostProcessor adminOauthUser() {
        return oauthUser("admin-user");
    }

    private static ClientRegistration githubClientRegistration() {
        return ClientRegistration.withRegistrationId("github")
                .clientId("test-client-id")
                .clientSecret("test-client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("read:user", "user:email")
                .authorizationUri("https://example.test/oauth/authorize")
                .tokenUri("https://example.test/oauth/token")
                .userInfoUri("https://example.test/userinfo")
                .userNameAttributeName("login")
                .clientName("GitHub")
                .build();
    }
}
