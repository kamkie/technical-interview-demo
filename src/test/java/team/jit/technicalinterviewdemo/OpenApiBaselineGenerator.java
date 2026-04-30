package team.jit.technicalinterviewdemo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

public final class OpenApiBaselineGenerator {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private OpenApiBaselineGenerator() {
    }

    public static void main(String[] args) throws Exception {
        Path outputPath = resolveOutputPath(args);
        ConfigurableApplicationContext context = new SpringApplicationBuilder(
                TechnicalInterviewDemoApplication.class,
                PostgresTestcontainersConfiguration.class
        )
                .profiles("test")
                .run("--server.port=0");

        try {
            int port = ((WebServerApplicationContext) context).getWebServer().getPort();
            HttpResponse<String> response = HTTP_CLIENT.send(
                    HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/v3/api-docs"))
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Expected 200 from /v3/api-docs but got " + response.statusCode());
            }

            Files.createDirectories(outputPath.getParent());
            Files.writeString(
                    outputPath,
                    OpenApiContractSupport.normalizeToPrettyJson(response.body()) + System.lineSeparator(),
                    StandardCharsets.UTF_8
            );
        } finally {
            context.close();
        }
    }

    private static Path resolveOutputPath(String[] args) {
        if (args.length == 0 || args[0].isBlank()) {
            return Path.of("src/test/resources/openapi/approved-openapi.json");
        }
        return Path.of(args[0]);
    }
}
