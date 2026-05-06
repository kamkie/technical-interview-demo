package team.jit.technicalinterviewdemo.performance;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.details;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.headerRegex;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.time.Duration;

public class AuthenticationRedirectSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http.baseUrl(PerformanceEnvironment.baseUrl()).disableFollowRedirect().userAgentHeader("gatling-phase-9-auth-redirect");

    private final ScenarioBuilder scenarioBuilder = scenario("oauth-redirect-start").exec(
            http("oauth2-github-redirect").get("/api/session/oauth2/authorization/github").check(status().is(302)).check(headerRegex("Location", "https://github.com/.*"))
    );

    {
        setUp(
                scenarioBuilder.injectOpen(
                        atOnceUsers(3), rampUsersPerSec(1).to(4).during(Duration.ofSeconds(15)), constantUsersPerSec(4).during(Duration.ofSeconds(15))
                )
        ).protocols(httpProtocol).assertions(
                global().successfulRequests().percent().gte(99.0), details("oauth2-github-redirect").responseTime().percentile3().lt(700)
        );
    }
}
