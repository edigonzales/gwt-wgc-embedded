package ch.so.agi.wgc.client;

import static elemental2.dom.DomGlobal.console;
import static org.jboss.elemento.Elements.*;
import static org.dominokit.domino.ui.style.Unit.px;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.button.ButtonSize;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.style.ColorScheme;
import org.dominokit.domino.ui.themes.Theme;
import org.gwtproject.safehtml.shared.SafeHtmlUtils;
import org.jboss.elemento.HtmlContentBuilder;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;

import ch.so.agi.wgc.shared.BackgroundMapConfig;
import ch.so.agi.wgc.shared.ConfigResponse;
import ch.so.agi.wgc.shared.ConfigService;
import ch.so.agi.wgc.shared.ConfigServiceAsync;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.Headers;
import elemental2.dom.RequestInit;
import ol.Coordinate;
import ol.Map;
import ol.MapBrowserEvent;
import ol.MapEvent;
import ol.events.Event;

public class AppEntryPoint implements EntryPoint {
    private MyMessages messages = GWT.create(MyMessages.class);
    private final ConfigServiceAsync configService = GWT.create(ConfigService.class);
    
    // Settings
    private List<BackgroundMapConfig> backgroundMaps;
    private String baseUrlWms;
    private String baseUrlFeatureInfo;
    private String baseUrlBigMap;
    private String baseUrlReport;
    
    private NumberFormat fmtDefault = NumberFormat.getDecimalFormat();
    private NumberFormat fmtPercent = NumberFormat.getFormat("#0.0");
    
    private String MAP_DIV_ID = "map";

    private WgcMap map;
    HTMLElement popup;
    
    public void onModuleLoad() {
        configService.configServer(new AsyncCallback<ConfigResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                console.error(caught.getMessage());
                DomGlobal.window.alert(caught.getMessage());
            }

            @Override
            public void onSuccess(ConfigResponse config) {
                backgroundMaps = config.getBackgroundMaps(); 
                baseUrlWms = config.getBaseUrlWms();
                baseUrlFeatureInfo = config.getBaseUrlFeatureInfo();
                baseUrlBigMap = config.getBaseUrlBigMap();
                baseUrlReport = config.getBaseUrlReport();
                init();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void init() {         
        Theme theme = new Theme(ColorScheme.WHITE);
        theme.apply();

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
        // TODO: falls nicht vorhanden eine Liste mit 1.

        if (Window.Location.getParameter("E") != null && Window.Location.getParameter("N") != null) {
            double easting = Double.valueOf(Window.Location.getParameter("E"));
            double northing = Double.valueOf(Window.Location.getParameter("N"));
            map.getView().setCenter(new Coordinate(easting,northing));
        }
        if (Window.Location.getParameter("zoom") != null) {
            map.getView().setZoom(Double.valueOf(Window.Location.getParameter("zoom")));
        }

        if (bgLayer != null) {
            map.setBackgroundLayer(bgLayer);
        }
        
        for (int i=0; i<layerList.size(); i++) {
            map.addForegroundLayer(layerList.get(i), opacityList.get(i));
        }
        
        BigMapLink bigMapLink = new BigMapLink(map, "In geo.so.ch/map ansehen");
        body().add(bigMapLink.element());
        
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

                updateURLWithoutReloading(newUrl);
                
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