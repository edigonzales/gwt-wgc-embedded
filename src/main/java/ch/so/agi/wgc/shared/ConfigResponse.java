package ch.so.agi.wgc.shared;

import java.util.List;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ConfigResponse implements IsSerializable {
    private List<BackgroundMap> backgroundMaps;
    
    public List<BackgroundMap> getBackgroundMaps() {
        return backgroundMaps;
    }

    public void setBackgroundMaps(List<BackgroundMap> backgroundMaps) {
        this.backgroundMaps = backgroundMaps;
    }
}
