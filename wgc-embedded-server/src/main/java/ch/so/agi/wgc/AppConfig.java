package ch.so.agi.wgc;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import ch.so.agi.wgc.settings.Settings;

@Component
@ConfigurationProperties("app")
public class AppConfig {
    private Settings settings;

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
