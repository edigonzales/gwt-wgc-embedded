package ch.so.agi.wgc;

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
    private String ID_ATTR_NAME = "id";

    private String baseUrlWms;
    private String baseUrlFeatureInfo;
    private String baseUrlReport;
    private String baseUrlBigMap;
    private String versionWms = "1.3.0";
        
    private String backgroundLayer;
    private List<String> foregroundLayers = new ArrayList<String>();
    private List<Double> foregroundLayerOpacities = new ArrayList<Double>();
    
    public WgcMap(MapOptions mapOptions, String baseUrlWms, String baseUrlFeatureInfo, String baseUrlReport, String baseUrlBigMap) {
        super(mapOptions);
        this.baseUrlWms = baseUrlWms;
        this.baseUrlFeatureInfo = baseUrlFeatureInfo;
        this.baseUrlReport = baseUrlReport;
        this.baseUrlBigMap = baseUrlBigMap;
    }
    
    /**
     * Set background layer of map.
     * 
     * @param id The identifier of the background layer (WMTS layer name).
     */
    public void setBackgroundLayer(String id) {
        Base layer = this.getMapLayerById(id);
        if (layer != null) {
            layer.setVisible(true);
            backgroundLayer = id;
        } else {
            DomGlobal.window.alert("Backgroundlayer '" + id + "' not found.");
        }
    }
    
    /**
     * Adds a WMS layer to the map.
     * 
     * @param id The identifier of the wms layer.
     * @param opacity The opacity of the wms layer.
     */
    public void addForegroundLayer(String layer, double opacity) {
        String id;
//        String encodedLayer;
        
        if (layer.startsWith("WMS")) {
            console.log(layer);
            
            String[] layerParts = layer.split("\\|\\|");
            
            if (layerParts[1] == null || layerParts[2] == null || layerParts[3] == null) {
                return;
            }
            
            this.baseUrlWms = layerParts[1];
            id = layerParts[2];
            this.versionWms = layerParts[3];
            
//            encodedLayer = layerParts[0] + "%7C%7" + URL.encode(layerParts[1]) + "%7C%7" + layerParts[2] + "%7C%7" + layerParts[3];
//            console.log(URL.decode(layerParts[1])); 
            
        } else {
            id = layer;
//            encodedLayer = layer;
        }              

        ImageWmsParams imageWMSParams = OLFactory.createOptions();
        imageWMSParams.setLayers(id);
        imageWMSParams.set("FORMAT", "image/png");
        imageWMSParams.set("TRANSPARENT", "true");
        imageWMSParams.set("VERSION", this.versionWms);

        ImageWmsOptions imageWMSOptions = OLFactory.createOptions();
        imageWMSOptions.setUrl(this.baseUrlWms);
        imageWMSOptions.setRatio(1.2f);
        imageWMSOptions.setParams(imageWMSParams);

        ImageWms imageWMSSource = new ImageWms(imageWMSOptions);

        LayerOptions layerOptions = OLFactory.createOptions();
        layerOptions.setSource(imageWMSSource);

        ol.layer.Image wmsLayer = new ol.layer.Image(layerOptions);
        wmsLayer.set(ID_ATTR_NAME, id);
        wmsLayer.setOpacity(opacity);

        this.addLayer(wmsLayer);
        
        this.foregroundLayers.add(layer);
        this.foregroundLayerOpacities.add(opacity);
    } 

    /**
     * Returns the base url of the feature info service.
     * 
     * @return The base url of the feature info service.
     */
    public String getBaseUrlFeatureInfo() {
        return baseUrlFeatureInfo;
    }
    
    /** 
     * Sets the base url of the report service.
     * 
     * @param baseUrlReport The base url of the report service.
     */
    public void setBaseUrlReport(String baseUrlReport) {
        this.baseUrlReport = baseUrlReport;
    }
    
    /**
     * Returns the base url of the report service.
     * @return The base url of the report service.
     */
    public String getBaseUrlReport() {
        return baseUrlReport;
    }
    
    /**
     * Returns the identifier of the current background layer. 
     * @return
     */
    public String getBackgroundLayer() {
        return backgroundLayer;
    }
    
    /**
     * Returns the identifiers of all foreground layers (wms layers).
     * @return Identifiers of all foreground layers.
     */
    public List<String> getForegroundLayers() {
        return foregroundLayers;
    }
    
    /**
     * Returns the opacity values of all foreground layers (wms layers).
     * @return Opacity values of all foreground layers.
     */
    public List<Double> getForgroundLayerOpacities() {
        return foregroundLayerOpacities;
    }
    
    /**
     * Creates the url to the official web gis client. Makes use of
     * our simple but nice so called "url interface".
     * 
     * @return The url to the official web gis client.
     */
    public String createBigMapUrl() {
        // Nur notwendig, weil im Web GIS Client die Hintergrundkarte
        // nicht mit dem WMTS-Layernamen angesprochen wird.
        String bg = "";
        if (backgroundLayer != null) {
            bg = backgroundLayer.substring(10);
        }
        
        // Zusammenbringen der Layer und der Layeropazität.
        String l = "";
        for (int i=0; i < foregroundLayers.size(); i++) {
            l += foregroundLayers.get(i);
            
            int transparency = (int) ((1.0 - foregroundLayerOpacities.get(i)) * 100.0);
            l += "[" + String.valueOf(transparency) + "]";
            
            if (i != foregroundLayers.size()-1) {
                l += ",";
            }
        }
        String urlBigMap = baseUrlBigMap + "?bl=" + bg + "&l=" + l;
        
        ol.Extent extent = this.getView().calculateExtent(this.getSize());
        double easting = extent.getLowerLeftX() + (extent.getUpperRightX() - extent.getLowerLeftX()) / 2;
        double northing = extent.getLowerLeftY() + (extent.getUpperRightY() - extent.getLowerLeftY()) / 2;        
        urlBigMap += "&c=" + easting + "," + northing;
        
        int scale = (int) (this.getView().getResolution() * (357.14 / 0.1)); // TODO Validate calculation. Habe ich aus einer Liste von mir...
        urlBigMap += "&s=" + String.valueOf(scale);
        
        return urlBigMap;
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
