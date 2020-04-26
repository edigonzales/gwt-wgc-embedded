package ch.so.agi.wgc.client;

import static elemental2.dom.DomGlobal.console;

import elemental2.dom.DomGlobal;
import ol.MapOptions;
import ol.OLFactory;
import ol.layer.Base;
import ol.layer.LayerOptions;
import ol.source.ImageWms;
import ol.source.ImageWmsOptions;
import ol.source.ImageWmsParams;

public class WgcMap extends ol.Map {
    private String ID_ATTR_NAME = "id";

    private String baseWmsUrl;
        
    public WgcMap(MapOptions mapOptions, String baseWmsUrl) {
        super(mapOptions);
        this.baseWmsUrl = baseWmsUrl;
    }
    
    public void changeBackgroundLayer(String id) {
        Base layer = this.getMapLayerById(id);
        if (layer != null) {
            layer.setVisible(true);
        } else {
            DomGlobal.window.alert("Backgroundlayer '" + id + "' not found.");
        }
    }
    
    public void addLayer(String id) {
        ImageWmsParams imageWMSParams = OLFactory.createOptions();
        imageWMSParams.setLayers(id);
        imageWMSParams.set("FORMAT", "image/png");
        imageWMSParams.set("TRANSPARENT", "true");

        ImageWmsOptions imageWMSOptions = OLFactory.createOptions();
        imageWMSOptions.setUrl(baseWmsUrl);
        imageWMSOptions.setRatio(1.2f);
        imageWMSOptions.setParams(imageWMSParams);

        ImageWms imageWMSSource = new ImageWms(imageWMSOptions);

        LayerOptions layerOptions = OLFactory.createOptions();
        layerOptions.setSource(imageWMSSource);

        ol.layer.Image wmsLayer = new ol.layer.Image(layerOptions);
        
        this.addLayer(wmsLayer);
    } 

    // TODO
    public String getVisibleLayer() {
        return null;
    }
    
    // Get Openlayers map layer by id.
    private Base getMapLayerById(String id) {
        ol.Collection<Base> layers = this.getLayers();
        for (int i = 0; i < layers.getLength(); i++) {
            Base item = layers.item(i);
            try {
                String layerId = item.get(ID_ATTR_NAME);                
                if (layerId == null) {
                    continue;
                }
                if (layerId.equalsIgnoreCase(id)) {
                    return item;
                }
            } catch (Exception e) {
                console.log(e.getMessage());
                console.log("should not reach here");
            }
        }
        return null;
    }
}
