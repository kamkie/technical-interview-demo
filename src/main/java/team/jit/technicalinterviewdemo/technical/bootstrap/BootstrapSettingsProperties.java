package team.jit.technicalinterviewdemo.technical.bootstrap;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.bootstrap")
public class BootstrapSettingsProperties {

    private Set<String> initialAdminIdentities = new LinkedHashSet<>();
    private final Seed seed = new Seed();

    public Set<String> normalizedInitialAdminIdentities() {
        return initialAdminIdentities.stream().map(String::trim).filter(value -> !value.isBlank()).map(value -> value.toLowerCase(Locale.ROOT)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<String> getInitialAdminIdentities() {
        return initialAdminIdentities;
    }

    public void setInitialAdminIdentities(Set<String> initialAdminIdentities) {
        this.initialAdminIdentities = initialAdminIdentities;
    }

    public Seed getSeed() {
        return seed;
    }

    public static class Seed {

        private boolean demoData;

        public boolean isDemoData() {
            return demoData;
        }

        public void setDemoData(boolean demoData) {
            this.demoData = demoData;
        }
    }
}
