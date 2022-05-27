package ch.so.agi.wgc;

import static elemental2.dom.DomGlobal.console;
import static elemental2.dom.DomGlobal.fetch;
import static org.jboss.elemento.Elements.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;

import ch.so.agi.wgc.settings.BackgroundMapSettings;
import ch.so.agi.wgc.settings.Settings;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import elemental2.dom.Location;
import elemental2.dom.Element;
import ol.Coordinate;
import ol.MapBrowserEvent;
import ol.MapEvent;

public class App implements EntryPoint {
    // Application settings
    public static interface SettingsMapper extends ObjectMapper<Settings> {}
    private SettingsMapper mapper;
    private Settings settings;
    private List<BackgroundMapSettings> backgroundMaps;
    private String baseUrlWms;
    private String baseUrlFeatureInfo;
    private String baseUrlBigMap;
    private String baseUrlReport;

    // Format settings
    private NumberFormat fmtDefault = NumberFormat.getDecimalFormat();
    private NumberFormat fmtPercent = NumberFormat.getFormat("#0.0");
    
    private String MAP_DIV_ID = "map";
    private WgcMap map;
    HTMLElement popup;

	public void onModuleLoad() {
        mapper = GWT.create(SettingsMapper.class);   

        Location location = DomGlobal.window.location;
        if (location.pathname.length() > 1) {
            location.pathname += "/"; 
        }

        console.log(location.toString());
        console.log(location.pathname);
        
        DomGlobal.fetch(location.pathname + "settings")
        .then(response -> {
            if (!response.ok) {
                DomGlobal.window.alert(response.statusText + ": " + response.body);
                return null;
            }
            return response.text();
        })
        .then(json -> {
            settings = mapper.read(json);
            baseUrlWms = settings.getBaseUrlWms();
            baseUrlFeatureInfo = settings.getBaseUrlFeatureInfo();
            baseUrlReport = settings.getBaseUrlReport();
            baseUrlBigMap = settings.getBaseUrlBigMap();
            backgroundMaps = settings.getBackgroundMaps();
                        
            init();
            
            return null;
        }).catch_(error -> {
            console.log(error);
            DomGlobal.window.alert(error.toString());
            return null;
        });
    }
	
    @SuppressWarnings("unchecked")
	public void init() {	    
//        HTMLElement logoDiv = div().css("logo")
//                .add(div()
//                        .add(img().attr("src", location.protocol + "//" + location.host + location.pathname + "Logo.png").attr("alt", "Logo Kanton")).element()).element();

        
        
        
        body().add(div().id(MAP_DIV_ID));
        
        map = new WgcMapBuilder()
                .setMapId(MAP_DIV_ID)
                .setBaseUrlWms(baseUrlWms)
                .setBaseUrlFeatureInfo(baseUrlFeatureInfo)
                .setBaseUrlReport(baseUrlReport)
                .setBaseUrlBigMap(baseUrlBigMap)
                .addBackgroundLayers(backgroundMaps)
                .build();

        String bgLayer = null;
        if (Window.Location.getParameter("bgLayer") != null) {
            bgLayer = Window.Location.getParameter("bgLayer").toString();
        }
        List<String> layerList = new ArrayList<String>();
        if (Window.Location.getParameter("layers") != null) {
            String layers = Window.Location.getParameter("layers").toString();
            layerList = Arrays.asList(layers.split(",", -1));
        }
        List<Double> opacityList = new ArrayList<Double>();
        if (Window.Location.getParameter("layers_opacity") != null) {
            String opacities = Window.Location.getParameter("layers_opacity").toString();
            List<String> rawList = Arrays.asList(opacities.split(","));
            for(int i=0; i<rawList.size(); i++) {
                opacityList.add(Double.parseDouble(rawList.get(i)));
            }
        }
        double easting;
        double northing;
        if (Window.Location.getParameter("E") != null && Window.Location.getParameter("N") != null) {
            easting = Double.valueOf(Window.Location.getParameter("E"));
            northing = Double.valueOf(Window.Location.getParameter("N"));
        } else {
            easting = 2613276;
            northing = 1238721;
        }
        map.getView().setCenter(new Coordinate(easting,northing));
        
        if (Window.Location.getParameter("zoom") != null) {
            map.getView().setZoom(Double.valueOf(Window.Location.getParameter("zoom")));
        }
        
        if (bgLayer != null) {
            map.setBackgroundLayer(bgLayer);
        }
        
        for (int i=0; i<layerList.size(); i++) {
            map.addForegroundLayer(layerList.get(i), opacityList.get(i));
        }
        
        if (Window.Location.getParameter("bigMapLink") == null) {
            BigMapLink bigMapLink = new BigMapLink(map, "In geo.so.ch/map ansehen");
            body().add(bigMapLink.element());
        } 

        map.addClickListener(new ol.event.EventListener<MapBrowserEvent>() {
            @Override
            public void onEvent(MapBrowserEvent event) {                
                if (popup != null) {
                   popup.remove(); 
                }
                popup = new Popup(map, event).element();
                body().add(popup);
            }
        });        
        
        map.addMoveEndListener(new ol.event.EventListener<MapEvent>() {
            @Override
            public void onEvent(MapEvent event) {
                ol.View view = map.getView();
                
                ol.Extent extent = view.calculateExtent(map.getSize());
                double easting = extent.getLowerLeftX() + (extent.getUpperRightX() - extent.getLowerLeftX()) / 2;
                double northing = extent.getLowerLeftY() + (extent.getUpperRightY() - extent.getLowerLeftY()) / 2;
                
                String newUrl = Window.Location.getProtocol() + "//" + Window.Location.getHost() + Window.Location.getPath();
                newUrl += "?bgLayer=" + map.getBackgroundLayer();
                newUrl += "&layers=" + String.join(",", map.getForegroundLayers());
                newUrl += "&layers_opacity=" + map.getForgroundLayerOpacities().stream().map(String::valueOf).collect(Collectors.joining(","));
                newUrl += "&E=" + String.valueOf(easting);
                newUrl += "&N=" + String.valueOf(northing);
                newUrl += "&zoom=" + String.valueOf(view.getZoom());

                String newUrlEncoded = URL.encode(newUrl);
                // TODO: Externe baseUrlWms wird nicht encoded. Ist das ein Problem?
                updateURLWithoutReloading(newUrlEncoded);
                
                Element bigMapLinkElement = DomGlobal.document.getElementById("bigMapLink");
                bigMapLinkElement.removeAttribute("href");
                bigMapLinkElement.setAttribute("href", map.createBigMapUrl());
            }
        });
	}
	
    private static native void updateURLWithoutReloading(String newUrl) /*-{
        $wnd.history.pushState(newUrl, "", newUrl);
    }-*/; 
}