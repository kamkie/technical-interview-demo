package team.jit.technicalinterviewdemo.technical.docs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.jit.technicalinterviewdemo.technical.security.SameSiteCsrfContract;

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
                        "Authenticated browser session cookie used by protected operations."
                                + " It is established through a configured identity provider login path"
                                + " under /api/session/oauth2/authorization/{registrationId} when the optional oauth profile"
                                + " is active. Unsafe browser writes also require the "
                                + SameSiteCsrfContract.HEADER_NAME
                                + " request header mirrored from the readable "
                                + SameSiteCsrfContract.COOKIE_NAME
                                + " cookie."
                );

        return new OpenAPI()
                .info(new Info()
                        .title("technical-interview-demo API")
                        .version(buildProperties.getVersion())
                        .description(
                                "Machine-readable contract for the demo application's supported external /api/**"
                                        + " surface. Internal-only overview, documentation, OpenAPI publication,"
                                        + " and actuator validation paths are intentionally excluded."
                        ))
                .components(new Components().addSecuritySchemes(SESSION_COOKIE_SCHEME, sessionCookieSecurityScheme));
    }
}
