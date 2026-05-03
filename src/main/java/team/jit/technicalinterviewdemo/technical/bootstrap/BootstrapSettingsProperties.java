package team.jit.technicalinterviewdemo.technical.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.bootstrap")
public class BootstrapSettingsProperties {

    private final Seed seed = new Seed();

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
