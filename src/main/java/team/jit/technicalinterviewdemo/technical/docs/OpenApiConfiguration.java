package team.jit.technicalinterviewdemo.technical.docs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    public static final String SESSION_COOKIE_SCHEME = "sessionCookie";

    @Bean
    OpenAPI technicalInterviewDemoOpenApi(BuildProperties buildProperties) {
        SecurityScheme sessionCookieSecurityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("technical-interview-demo-session")
                .description(
                        "Authenticated browser session established through GET /oauth2/authorization/github"
                                + " when the oauth profile is active."
                );

        return new OpenAPI()
                .info(new Info()
                        .title("technical-interview-demo API")
                        .version(buildProperties.getVersion())
                        .description(
                                "Machine-readable contract for the demo application's public and secured API surface."
                                        + " Read endpoints stay public, while protected operations require the session"
                                        + " cookie created by the GitHub OAuth login flow."
                        ))
                .components(new Components().addSecuritySchemes(SESSION_COOKIE_SCHEME, sessionCookieSecurityScheme));
    }
}
