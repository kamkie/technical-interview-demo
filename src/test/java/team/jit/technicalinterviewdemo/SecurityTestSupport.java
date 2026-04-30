package team.jit.technicalinterviewdemo;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;

import java.util.Map;

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
        DefaultOAuth2User oauth2User = new DefaultOAuth2User(
                AuthorityUtils.createAuthorityList("ROLE_USER"),
                Map.of("login", "demo-user"),
                "login"
        );
        return oauth2Login().oauth2User(oauth2User);
    }
}
