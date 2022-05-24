package ch.so.agi.wgc.settings;

import java.util.ArrayList;
import java.util.List;

public class Settings {
    private List<BackgroundMapSettings> backgroundMaps = new ArrayList<BackgroundMapSettings>();
    
    private String baseUrlWms;
    
    private String baseUrlFeatureInfo;
    
    private String baseUrlBigMap;
    
    private String baseUrlReport;

    public List<BackgroundMapSettings> getBackgroundMaps() {
        return backgroundMaps;
    }

    public void setBackgroundMaps(List<BackgroundMapSettings> backgroundMaps) {
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
