package ch.so.agi.wgc;

import java.util.HashMap;
import java.util.List;

import ch.so.agi.wgc.settings.BackgroundMapSettings;
import ol.Collection;
import ol.Coordinate;
import ol.Extent;
import ol.MapOptions;
import ol.OLFactory;
import ol.View;
import ol.ViewOptions;
import ol.control.Control;
import ol.interaction.DefaultInteractionsOptions;
import ol.interaction.Interaction;
import ol.layer.LayerOptions;
import ol.layer.Tile;
import ol.proj.Projection;
import ol.proj.ProjectionOptions;
import ol.source.Wmts;
import ol.source.WmtsOptions;
import ol.tilegrid.TileGrid;
import ol.tilegrid.WmtsTileGrid;
import ol.tilegrid.WmtsTileGridOptions;
import proj4.Proj4;

public class WgcMapBuilder {
    private String BACKGROUND_LAYER_ATTR_NAME = "bgLayer";
    private String ID_ATTR_NAME = "id";
    
    private Projection projection;
    private String mapId;
    private HashMap<String, Tile> backgroundLayers = new HashMap<String, Tile>();
    private String baseUrlWms; 
    private String baseUrlFeatureInfo; 
    private String baseUrlReport; 
    private String baseUrlBigMap;
    
    public WgcMapBuilder() {
        Proj4.defs("EPSG:2056", "+proj=somerc +lat_0=46.95240555555556 +lon_0=7.439583333333333 +k_0=1 +x_0=2600000 +y_0=1200000 +ellps=bessel +towgs84=674.374,15.056,405.346,0,0,0,0 +units=m +no_defs");
        
        ProjectionOptions projectionOptions = OLFactory.createOptions();
        projectionOptions.setCode("EPSG:2056");
        projectionOptions.setUnits("m");
        projectionOptions.setExtent(new Extent(2420000, 1030000, 2900000, 1350000));

        projection = new Projection(projectionOptions);
    }
    
    public WgcMapBuilder setMapId(String mapId) {
        this.mapId = mapId;
        return this;
    }
    
    public WgcMapBuilder setBaseUrlWms(String baseUrlWms) {
        this.baseUrlWms = baseUrlWms; 
        return this;
    }
    
    public WgcMapBuilder setBaseUrlFeatureInfo(String baseUrlFeatureInfo) {
        this.baseUrlFeatureInfo = baseUrlFeatureInfo; 
        return this;
    }
    
    public WgcMapBuilder setBaseUrlReport(String baseUrlReport) {
        this.baseUrlReport = baseUrlReport;
        return this;
    }
   
    public WgcMapBuilder setBaseUrlBigMap(String baseUrlBigMap) {
        this.baseUrlBigMap = baseUrlBigMap; 
        return this;
    }
    
    public WgcMapBuilder addBackgroundLayers(List<BackgroundMapSettings> backgroundMaps) {        
        for (BackgroundMapSettings config : backgroundMaps) {
            WmtsOptions wmtsOptions = OLFactory.createOptions();
            wmtsOptions.setUrl(config.getUrl());
            wmtsOptions.setLayer(config.getLayer());
            wmtsOptions.setRequestEncoding(config.getRequestEncoding());
            wmtsOptions.setFormat(config.getFormat());
            wmtsOptions.setMatrixSet(config.getMatrixSet());
            wmtsOptions.setStyle(config.getStyle());
            wmtsOptions.setProjection(projection);
            wmtsOptions.setWrapX(true);
            wmtsOptions.setTileGrid(createWmtsTileGrid(projection));
            
            Wmts wmtsSource = new Wmts(wmtsOptions);
            
            LayerOptions wmtsLayerOptions = OLFactory.createOptions();
            wmtsLayerOptions.setSource(wmtsSource);

            Tile wmtsLayer = new Tile(wmtsLayerOptions);
            wmtsLayer.setOpacity(1);
            wmtsLayer.setVisible(false);
            wmtsLayer.set(BACKGROUND_LAYER_ATTR_NAME, true);
            wmtsLayer.set(ID_ATTR_NAME, config.getId());
            
            backgroundLayers.put(config.getId(), wmtsLayer);
        }
        return this;
    }
    
    public WgcMap build() {        
        ViewOptions viewOptions = OLFactory.createOptions();
        viewOptions.setProjection(projection);
        viewOptions.setResolutions(new double[] { 4000.0, 2000.0, 1000.0, 500.0, 250.0, 100.0, 50.0, 20.0, 10.0, 5.0, 2.5, 1.0, 0.5, 0.25, 0.1 });
        View view = new View(viewOptions);
        Coordinate centerCoordinate = new Coordinate(2616491, 1240287);

        view.setCenter(centerCoordinate);
        view.setZoom(6);
       
        MapOptions mapOptions = OLFactory.createOptions();
        mapOptions.setTarget(mapId);
        mapOptions.setView(view);
        mapOptions.setControls(new Collection<Control>());

        DefaultInteractionsOptions interactionOptions = new ol.interaction.DefaultInteractionsOptions();
        interactionOptions.setPinchRotate(false);
        mapOptions.setInteractions(Interaction.defaults(interactionOptions));

        WgcMap map = new WgcMap(mapOptions, baseUrlWms, baseUrlFeatureInfo, baseUrlReport, baseUrlBigMap);

        backgroundLayers.forEach((key, value) -> {      
            map.addLayer(value);
        });        
        return map;
    }
    
    private static TileGrid createWmtsTileGrid(Projection projection) {
        WmtsTileGridOptions wmtsTileGridOptions = OLFactory.createOptions();

        double resolutions[] = new double[] { 4000.0, 2000.0, 1000.0, 500.0, 250.0, 100.0, 50.0, 20.0, 10.0, 5.0, 2.5, 1.0, 0.5, 0.25, 0.1 };
        String[] matrixIds = new String[resolutions.length];

        for (int z = 0; z < resolutions.length; ++z) {
            matrixIds[z] = String.valueOf(z);
        }

        Coordinate tileGridOrigin = projection.getExtent().getTopLeft();
        wmtsTileGridOptions.setOrigin(tileGridOrigin);
        wmtsTileGridOptions.setResolutions(resolutions);
        wmtsTileGridOptions.setMatrixIds(matrixIds);

        return new WmtsTileGrid(wmtsTileGridOptions);
    }
}
