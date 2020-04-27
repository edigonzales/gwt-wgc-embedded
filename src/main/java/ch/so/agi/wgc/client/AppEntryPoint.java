package ch.so.agi.wgc.client;

import static elemental2.dom.DomGlobal.console;
import static org.jboss.elemento.Elements.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dominokit.domino.ui.style.ColorScheme;
import org.dominokit.domino.ui.themes.Theme;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import ch.so.agi.wgc.shared.BackgroundMapConfig;
import ch.so.agi.wgc.shared.ConfigResponse;
import ch.so.agi.wgc.shared.ConfigService;
import ch.so.agi.wgc.shared.ConfigServiceAsync;
import elemental2.dom.DomGlobal;
import ol.Map;
import ol.MapBrowserEvent;
import ol.MapEvent;
import ol.events.Event;

public class AppEntryPoint implements EntryPoint {
    private MyMessages messages = GWT.create(MyMessages.class);
    private final ConfigServiceAsync configService = GWT.create(ConfigService.class);
    
    private List<BackgroundMapConfig> backgroundMapsConfig;
    
    private NumberFormat fmtDefault = NumberFormat.getDecimalFormat();
    private NumberFormat fmtPercent = NumberFormat.getFormat("#0.0");
    
    private String MAP_DIV_ID = "map";

    private WgcMap map;
    
    public void onModuleLoad() {
        configService.configServer(new AsyncCallback<ConfigResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                console.error(caught.getMessage());
                DomGlobal.window.alert(caught.getMessage());
            }

            @Override
            public void onSuccess(ConfigResponse result) {
                backgroundMapsConfig = result.getBackgroundMaps();                
                init();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void init() { 
        console.log("init");
        
        Theme theme = new Theme(ColorScheme.WHITE);
        theme.apply();

        body().add(div().id(MAP_DIV_ID));

        // TODO
        // Use elemental2: DomGlobal.window.location.getSearch()
        // new URLSearchParams() not available in RC1 (?)        
        String bgLayer = "";
        if (Window.Location.getParameter("bgLayer") != null) {
            bgLayer = Window.Location.getParameter("bgLayer").toString();
        } else {
            DomGlobal.window.alert("bgLayer missing");
            console.error("bgLayer missing");
            return;
        }
        List<String> layerList = new ArrayList<String>();
        if (Window.Location.getParameter("layers") != null) {
            String layers = Window.Location.getParameter("layers").toString();
            layerList = Arrays.asList(layers.split(",", -1));
            console.log("layers: ", layerList);
        }
        List<Double> opacityList = new ArrayList<Double>();
        if (Window.Location.getParameter("layers_opacity") != null) {
            String opacities = Window.Location.getParameter("layers_opacity").toString();
            List<String> rawList = Arrays.asList(opacities.split(","));
            for(int i=0; i<rawList.size(); i++) {
                opacityList.add(Double.parseDouble(rawList.get(i)));
            }
            console.log("opacities: ", opacityList);
        }

//        console.log(Window.Location.getParameterMap().toString());
        
        
        
        map = new WgcMapBuilder()
                .setMapId(MAP_DIV_ID)
                .addBackgroundLayers(backgroundMapsConfig)
                .build();

        map.changeBackgroundLayer(bgLayer);
        
        for (int i=0; i<layerList.size(); i++) {
            map.addForegroundLayer(layerList.get(i), opacityList.get(i));
        }
//
//        layerList.forEach(l -> {
////            console.log(l);
//            map.addLayer(l, );
//        });
        
        String bigMapUrl = map.createBigMapUrl();
        body().add(new BigMapLink(map, bigMapUrl).element());
        
        
        
        // TODO getfeatureinfo
        // - url in config
        // - Den Rest selber zusammenstöpseln (und berechnen).
        // - fetch()
        map.addClickListener(new ol.event.EventListener<MapBrowserEvent>() {
            @Override
            public void onEvent(MapBrowserEvent event) {
                console.log(event.getCoordinate().toString());
                
                double resolution = map.getView().getResolution();
                console.log(map.getView().getResolution());

                // 50/51/101-Ansatz ist anscheinend bei OpenLayers normal.
                // -> siehe baseUrlFeatureInfo resp. ein Original-Request
                // im Web GIS Client.
                double minX = event.getCoordinate().getX() - 50 * resolution;
                double maxX = event.getCoordinate().getX() + 51 * resolution;
                double minY = event.getCoordinate().getY() - 50 * resolution;
                double maxY = event.getCoordinate().getY() + 51 * resolution;

                String baseUrlFeatureInfo = map.getBaseUrlFeatureInfo();
                List<String> foregroundLayers = map.getForegroundLayers();
                console.log(foregroundLayers);
                String layers = String.join(",", foregroundLayers);
                String urlFeatureInfo = baseUrlFeatureInfo + "&layers=" + layers;
                urlFeatureInfo += "&query_layers=" + layers;
                urlFeatureInfo += "&bbox=" + minX + "," + minY + "," + maxX + "," + maxY;
                
                console.log(urlFeatureInfo);
                
                
                
                
            }
        });        
        
        // TODO update window.location
        // Reicht MoveEndListener?
        map.addMapZoomEndListener(new ol.event.EventListener<MapEvent>() {
            @Override
            public void onEvent(MapEvent event) {
//                console.log("addMapZoomEndListener...");
            }
        });
        
        map.addMoveEndListener(new ol.event.EventListener<MapEvent>() {
            @Override
            public void onEvent(MapEvent event) {
//                console.log("addMoveEndListener...");
            }
        });
        
    }

   private static native void updateURLWithoutReloading(String newUrl) /*-{
        $wnd.history.pushState(newUrl, "", newUrl);
    }-*/;
}