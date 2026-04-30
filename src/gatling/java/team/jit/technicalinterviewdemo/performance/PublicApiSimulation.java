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

public class PublicApiSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl(PerformanceEnvironment.baseUrl())
            .acceptHeader("application/json")
            .userAgentHeader("gatling-phase-9-public-api");

    private final ScenarioBuilder scenarioBuilder = scenario("public-api-read-flow")
            .exec(
                    http("list-books")
                            .get("/api/books?page=0&size=20")
                            .check(status().is(200))
                            .check(headerRegex("Content-Type", "application/json.*"))
            )
            .pause(Duration.ofMillis(200))
            .exec(
                    http("search-books")
                            .get("/api/books?title=clean&category=java&yearFrom=2000&yearTo=2020")
                            .check(status().is(200))
                            .check(headerRegex("Content-Type", "application/json.*"))
            )
            .pause(Duration.ofMillis(200))
            .exec(
                    http("lookup-localization-message")
                            .get("/api/localization-messages/key/error.request.invalid/lang/pl")
                            .check(status().is(200))
                            .check(headerRegex("Content-Type", "application/json.*"))
            );

    {
        setUp(
                scenarioBuilder.injectOpen(
                        atOnceUsers(5),
                        rampUsersPerSec(1).to(6).during(Duration.ofSeconds(20)),
                        constantUsersPerSec(6).during(Duration.ofSeconds(20))
                )
        )
                .protocols(httpProtocol)
                .assertions(
                        global().successfulRequests().percent().gte(99.0),
                        details("list-books").responseTime().percentile3().lt(800),
                        details("search-books").responseTime().percentile3().lt(900),
                        details("lookup-localization-message").responseTime().percentile3().lt(500)
                );
    }
}
