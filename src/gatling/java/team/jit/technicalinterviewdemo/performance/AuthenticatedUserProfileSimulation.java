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

import java.time.Duration;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class AuthenticatedUserProfileSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl(PerformanceEnvironment.baseUrl())
            .acceptHeader("application/json")
            .userAgentHeader("gatling-phase-9-authenticated-profile")
            .header("Cookie", "technical-interview-demo-session=" + requiredSessionCookie());

    private final ScenarioBuilder scenarioBuilder = scenario("authenticated-user-profile")
            .exec(
                    http("current-user-profile")
                            .get("/api/users/me")
                            .check(status().is(200))
                            .check(headerRegex("Content-Type", "application/json.*"))
            );

    {
        setUp(
                scenarioBuilder.injectOpen(
                        atOnceUsers(3),
                        rampUsersPerSec(1).to(3).during(Duration.ofSeconds(15)),
                        constantUsersPerSec(3).during(Duration.ofSeconds(15))
                )
        )
                .protocols(httpProtocol)
                .assertions(
                        global().successfulRequests().percent().gte(99.0),
                        details("current-user-profile").responseTime().percentile3().lt(700)
                );
    }

    private static String requiredSessionCookie() {
        if (!PerformanceEnvironment.hasSessionCookie()) {
            throw new IllegalStateException(
                    "Set -Dapp.sessionCookie=<technical-interview-demo-session cookie> before running AuthenticatedUserProfileSimulation."
            );
        }
        return PerformanceEnvironment.sessionCookie();
    }
}
