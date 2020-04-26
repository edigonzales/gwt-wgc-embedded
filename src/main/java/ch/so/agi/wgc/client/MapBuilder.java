package ch.so.agi.wgc.client;

import static elemental2.dom.DomGlobal.console;

import java.util.HashMap;
import java.util.List;

import ch.so.agi.wgc.shared.BackgroundMapConfig;
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

public class MapBuilder {
    private Projection projection;
    private ol.Map map;
    private String mapId;
    private HashMap<String, Tile> backgroundLayers = new HashMap<String, Tile>();
    
    public MapBuilder() {
        Proj4.defs("EPSG:2056", "+proj=somerc +lat_0=46.95240555555556 +lon_0=7.439583333333333 +k_0=1 +x_0=2600000 +y_0=1200000 +ellps=bessel +towgs84=674.374,15.056,405.346,0,0,0,0 +units=m +no_defs");
        
        ProjectionOptions projectionOptions = OLFactory.createOptions();
        projectionOptions.setCode("EPSG:2056");
        projectionOptions.setUnits("m");
        projectionOptions.setExtent(new Extent(2420000, 1030000, 2900000, 1350000));

        projection = new Projection(projectionOptions);
    }
    
    public MapBuilder setMapId(String mapId) {
        this.mapId = mapId;
        return this;
    }
    
    public MapBuilder addBackgroundLayers(List<BackgroundMapConfig> backgroundMapsConfig) {
        console.log(backgroundMapsConfig);
        
        for (BackgroundMapConfig mapConfig : backgroundMapsConfig) {
//            mapConfig.get
        }
        
        return this;
    }
    
    
    public ol.Map build() {        
        WmtsOptions wmtsOptions = OLFactory.createOptions();
        wmtsOptions.setUrl("https://geo.so.ch/api/wmts/1.0.0/{Layer}/default/2056/{TileMatrix}/{TileRow}/{TileCol}");
        wmtsOptions.setLayer("ch.so.agi.hintergrundkarte_sw");
        wmtsOptions.setRequestEncoding("REST");
        wmtsOptions.setFormat("image/png");
        wmtsOptions.setMatrixSet("EPSG:2056");
        wmtsOptions.setStyle("default");
        wmtsOptions.setProjection(projection);
        wmtsOptions.setWrapX(true);
        wmtsOptions.setTileGrid(createWmtsTileGrid(projection));
        
        Wmts wmtsSource = new Wmts(wmtsOptions);
        
        LayerOptions wmtsLayerOptions = OLFactory.createOptions();
        wmtsLayerOptions.setSource(wmtsSource);

        Tile wmtsLayer = new Tile(wmtsLayerOptions);
        wmtsLayer.setOpacity(1.0);
        wmtsLayer.set("bgLayer", true);

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

        ol.Map map = new ol.Map(mapOptions);
        map.addLayer(wmtsLayer);
        
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
