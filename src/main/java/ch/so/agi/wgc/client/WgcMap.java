package ch.so.agi.wgc.client;

import static elemental2.dom.DomGlobal.console;

import java.util.ArrayList;
import java.util.List;

import elemental2.dom.DomGlobal;
import ol.MapOptions;
import ol.OLFactory;
import ol.layer.Base;
import ol.layer.LayerOptions;
import ol.source.ImageWms;
import ol.source.ImageWmsOptions;
import ol.source.ImageWmsParams;

public class WgcMap extends ol.Map {
    private String BACKGROUND_LAYER_ATTR_NAME = "bgLayer";
    private String TITLE_ATTR_NAME = "title";
    private String ID_ATTR_NAME = "id";

    private String baseUrlWms;
    private String baseUrlFeatureInfo;
    private String baseUrlBigMap;
        
    private String backgroundLayer;
    private List<String> foregroundLayers = new ArrayList<String>();
    private List<Double> foregroundLayerOpacities = new ArrayList<Double>();
    
    public WgcMap(MapOptions mapOptions, String baseUrlWms, String baseUrlFeatureInfo, String baseUrlBigMap) {
        super(mapOptions);
        this.baseUrlWms = baseUrlWms;
        this.baseUrlFeatureInfo = baseUrlFeatureInfo;
        this.baseUrlBigMap = baseUrlBigMap;
    }
    
    public void changeBackgroundLayer(String id) {
        Base layer = this.getMapLayerById(id);
        if (layer != null) {
            layer.setVisible(true);
            backgroundLayer = id;
        } else {
            DomGlobal.window.alert("Backgroundlayer '" + id + "' not found.");
        }
    }
    
    public void addForegroundLayer(String id, double opacity) {
        ImageWmsParams imageWMSParams = OLFactory.createOptions();
        imageWMSParams.setLayers(id);
        imageWMSParams.set("FORMAT", "image/png");
        imageWMSParams.set("TRANSPARENT", "true");

        ImageWmsOptions imageWMSOptions = OLFactory.createOptions();
        imageWMSOptions.setUrl(baseUrlWms);
        imageWMSOptions.setRatio(1.2f);
        imageWMSOptions.setParams(imageWMSParams);

        ImageWms imageWMSSource = new ImageWms(imageWMSOptions);

        LayerOptions layerOptions = OLFactory.createOptions();
        layerOptions.setSource(imageWMSSource);

        ol.layer.Image wmsLayer = new ol.layer.Image(layerOptions);
        wmsLayer.set(ID_ATTR_NAME, id);
        wmsLayer.setOpacity(opacity);
        
        this.addLayer(wmsLayer);
        
        this.foregroundLayers.add(id);
        this.foregroundLayerOpacities.add(opacity);
    } 

    public String getBaseUrlFeatureInfo() {
        return baseUrlFeatureInfo;
    }
    
    /*
     * 
     */
    public List<String> getForegroundLayers() {
//        List<String> layerList = new ArrayList<String>();
//        ol.Collection<Base> layers = this.getLayers();
//        for (int i = 0; i < layers.getLength(); i++) {  
//            Base item = layers.item(i);
//            if (item.get(BACKGROUND_LAYER_ATTR_NAME) != null && (boolean) item.get(BACKGROUND_LAYER_ATTR_NAME) == true) {
//                // do nothing: background layer
//            } else {
//                if (item.getVisible()) {
//                    layerList.add((String)item.get(ID_ATTR_NAME)); 
//                }
//            }
//        }
//        return layerList;
        return foregroundLayers;
    }
    
    public String createBigMapUrl() {
        // Nur notwendig, weil im Web GIS Client die Hintergrundkarte
        // nicht mit dem WMTS-Layernamen angesprochen wird.
        String bg = backgroundLayer.substring(10);
        
        // Zusammenbringen der Layer und der Layeropazität.
        String l = "";
        for (int i=0; i < foregroundLayers.size(); i++) {
            l += foregroundLayers.get(i);
            
            int transparency = (int) ((1.0 - foregroundLayerOpacities.get(i)) * 255.0);
            l += "[" + String.valueOf(transparency) + "]";
            
            if (i != foregroundLayers.size()-1) {
                l += ",";
            }
        }
        console.log(l);
        
        baseUrlBigMap += "?bl=" + bg + "&l=" + l;
        return baseUrlBigMap;
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
