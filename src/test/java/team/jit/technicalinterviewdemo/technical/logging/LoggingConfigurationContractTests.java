package team.jit.technicalinterviewdemo.technical.logging;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class LoggingConfigurationContractTests {

    @Test
    void baseConfigurationKeepsAnsiDetectionEnabled() throws IOException {
        Properties properties = loadProperties("application.properties");

        assertThat(properties.getProperty("spring.output.ansi.enabled")).isEqualTo("DETECT");
    }

    @Test
    void productionConfigurationKeepsRootLoggingAtInfo() throws IOException {
        Properties properties = loadProperties("application-prod.properties");

        assertThat(properties.getProperty("logging.level.root")).isEqualTo("INFO");
        assertThat(properties.getProperty("logging.level.org.springframework")).isEqualTo("WARN");
    }

    private Properties loadProperties(String classpathLocation) throws IOException {
        Properties properties = new Properties();
        ClassPathResource resource = new ClassPathResource(classpathLocation);
        try (InputStream inputStream = resource.getInputStream()) {
            properties.load(inputStream);
        }
        return properties;
    }
}
