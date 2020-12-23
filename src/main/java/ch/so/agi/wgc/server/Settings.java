package ch.so.agi.wgc.server;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.google.gwt.user.client.rpc.IsSerializable;

import ch.so.agi.wgc.shared.BackgroundMapConfig;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "app")
public class Settings implements IsSerializable {
    private List<BackgroundMapConfig> backgroundMaps = new ArrayList<BackgroundMapConfig>();
    
    private String baseUrlWms;
    
    private String baseUrlFeatureInfo;
    
    private String baseUrlBigMap;
    
    private String baseUrlReport;

    
    public Settings() {}

    public List<BackgroundMapConfig> getBackgroundMaps() {
        return backgroundMaps;
    }

    public void setBackgroundMaps(List<BackgroundMapConfig> backgroundMaps) {
        this.backgroundMaps = backgroundMaps;
    }

    public String getBaseUrlWms() {
        return baseUrlWms;
    }

    public void setBaseUrlWms(String baseUrlWms) {
        this.baseUrlWms = baseUrlWms;
    }

    public String getBaseUrlFeatureInfo() {
        return baseUrlFeatureInfo;
    }

    public void setBaseUrlFeatureInfo(String baseUrlFeatureInfo) {
        this.baseUrlFeatureInfo = baseUrlFeatureInfo;
    }

    public String getBaseUrlBigMap() {
        return baseUrlBigMap;
    }

    public void setBaseUrlBigMap(String baseUrlBigMap) {
        this.baseUrlBigMap = baseUrlBigMap;
    }

    public String getBaseUrlReport() {
        return baseUrlReport;
    }

    public void setBaseUrlReport(String baseUrlReport) {
        this.baseUrlReport = baseUrlReport;
    }
}
