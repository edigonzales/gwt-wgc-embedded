package ch.so.agi.wgc.shared;

import java.util.List;
import com.google.gwt.user.client.rpc.IsSerializable;

public class SettingsResponse implements IsSerializable {
    private List<BackgroundMapConfig> backgroundMaps;
    
    private String baseUrlWms;
    
    private String baseUrlFeatureInfo;
    
    private String baseUrlBigMap;
    
    private String baseUrlReport;
    
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
