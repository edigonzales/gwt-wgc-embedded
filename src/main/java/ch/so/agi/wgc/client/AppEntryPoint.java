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
    
    private List<BackgroundMapConfig> backgroundMapsConfig;
    
    private NumberFormat fmtDefault = NumberFormat.getDecimalFormat();
    private NumberFormat fmtPercent = NumberFormat.getFormat("#0.0");
    
    private String MAP_DIV_ID = "map";

    private WgcMap map;
    HTMLDivElement popup;
    
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
        Theme theme = new Theme(ColorScheme.WHITE);
        theme.apply();

        body().add(div().id(MAP_DIV_ID));

        map = new WgcMapBuilder()
                .setMapId(MAP_DIV_ID)
                .addBackgroundLayers(backgroundMapsConfig)
                .build();

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
        }
        List<Double> opacityList = new ArrayList<Double>();
        if (Window.Location.getParameter("layers_opacity") != null) {
            String opacities = Window.Location.getParameter("layers_opacity").toString();
            List<String> rawList = Arrays.asList(opacities.split(","));
            for(int i=0; i<rawList.size(); i++) {
                opacityList.add(Double.parseDouble(rawList.get(i)));
            }
        }
        if (Window.Location.getParameter("E") != null && Window.Location.getParameter("N") != null) {
            double easting = Double.valueOf(Window.Location.getParameter("E"));
            double northing = Double.valueOf(Window.Location.getParameter("N"));
            map.getView().setCenter(new Coordinate(easting,northing));
        }
        if (Window.Location.getParameter("zoom") != null) {
            map.getView().setZoom(Double.valueOf(Window.Location.getParameter("zoom")));
        }

        map.setBackgroundLayer(bgLayer);
        
        for (int i=0; i<layerList.size(); i++) {
            map.addForegroundLayer(layerList.get(i), opacityList.get(i));
        }
        
        BigMapLink bigMapLink = new BigMapLink(map);
        body().add(bigMapLink.element());
        
        map.addClickListener(new ol.event.EventListener<MapBrowserEvent>() {
            @Override
            public void onEvent(MapBrowserEvent event) {                
                if (popup != null) {
                   popup.remove(); 
                }
                
                HTMLElement icon = Icons.ALL.close_mdi().element();
                icon.style.verticalAlign = "middle";
                icon.addEventListener("click", new EventListener() {
                    @Override
                    public void handleEvent(elemental2.dom.Event evt) {
                        popup.remove();
                    }
                });
                
                HTMLElement closeButton = span().id("popupHeaderButtonSpan").add(icon).element();                    
                HtmlContentBuilder popupBuilder = div().id("popup");
                popupBuilder.add(
                        div().id("popupHeader")
                        .add(span().id("popupHeaderTextSpan").textContent("Objektinformation"))
                        .add(closeButton)
                        ); 
                
                popup = (HTMLDivElement) popupBuilder.element();
                popup.style.position = "absolute";
                popup.style.top = "5px";
                popup.style.left = "5px";
                
                double resolution = map.getView().getResolution();
                //console.log(map.getView().getResolution());

                // 50/51/101-Ansatz ist anscheinend bei OpenLayers normal.
                // -> siehe baseUrlFeatureInfo resp. ein Original-Request
                // im Web GIS Client.
                double minX = event.getCoordinate().getX() - 50 * resolution;
                double maxX = event.getCoordinate().getX() + 51 * resolution;
                double minY = event.getCoordinate().getY() - 50 * resolution;
                double maxY = event.getCoordinate().getY() + 51 * resolution;

                String baseUrlFeatureInfo = map.getBaseUrlFeatureInfo();
                List<String> foregroundLayers = map.getForegroundLayers();
                String layers = String.join(",", foregroundLayers);
                String urlFeatureInfo = baseUrlFeatureInfo + "&layers=" + layers;
                urlFeatureInfo += "&query_layers=" + layers;
                urlFeatureInfo += "&bbox=" + minX + "," + minY + "," + maxX + "," + maxY;
                                
                RequestInit requestInit = RequestInit.create();
                Headers headers = new Headers();
                headers.append("Content-Type", "application/x-www-form-urlencoded"); 
                requestInit.setHeaders(headers);
                
                DomGlobal.fetch(urlFeatureInfo)
                .then(response -> {
                    if (!response.ok) {
                        return null;
                    }
                    return response.text();
                })
                .then(xml -> {
                    Document messageDom = XMLParser.parse(xml);
                    
                    if (messageDom.getElementsByTagName("Feature").getLength() == 0) {
                        popup.appendChild(div().css("popupNoContent").textContent("Keine weiteren Informationen").element());
                    }
                    
                    // TODO: nicht gruppieren nach Layer. Jedes Feature hat einen layerHeader.
                    
                    for (int i=0; i<messageDom.getElementsByTagName("Layer").getLength(); i++) {
                        Node layerNode = messageDom.getElementsByTagName("Layer").item(i);
                        String layerName = ((com.google.gwt.xml.client.Element) layerNode).getAttribute("layername"); 
                        String layerTitle = ((com.google.gwt.xml.client.Element) layerNode).getAttribute("name"); 
                        
                        if (layerNode.getChildNodes().getLength() == 0) {
                            continue;
                        };
                        
//                        popup.appendChild(div().css("popupLayerHeader").textContent(layerTitle).element());
                        
//                        NodeList featureNodes = ((com.google.gwt.xml.client.Element) layerNode).getElementsByTagName("Feature");
//                        for (int j=0; j<featureNodes.getLength(); j++) {
//                            Node featureNode = featureNodes.item(j);
//                            String foo = ((com.google.gwt.xml.client.Element) featureNode).getTagName();
//                            console.log(foo);
//                        }
                        
                        // TODO: rename featureNodes
                        NodeList htmlNodes = ((com.google.gwt.xml.client.Element) layerNode).getElementsByTagName("HtmlContent");
                        for (int j=0; j<htmlNodes.getLength(); j++) {
                                                        
                            Text htmlNode = (Text) htmlNodes.item(j).getFirstChild();   
                            popup.appendChild(div().css("popupLayerHeader").textContent(layerTitle).element());     
                            
                            HtmlContentBuilder popupContentBuilder = div().css("popupContent");
                            
                            HTMLDivElement featureInfoHtml = div().innerHtml(SafeHtmlUtils.fromTrustedString(htmlNode.getData())).element();
                            popupContentBuilder.add(featureInfoHtml);

                            com.google.gwt.xml.client.Element layerElement = ((com.google.gwt.xml.client.Element) layerNode);
                            if (layerElement.getAttribute("featurereport") != null) {
                                String baseUrlReport = "https://geo.so.ch/api/v1/document/"; // TODO: Config
                                
                                double x = event.getCoordinate().getX();
                                double y = event.getCoordinate().getY();
                                
                                com.google.gwt.xml.client.Element featureNode = ((com.google.gwt.xml.client.Element) htmlNodes.item(j).getParentNode());
                                String featureId = featureNode.getAttribute("id");
                                
                                String urlReport = baseUrlReport + layerElement.getAttribute("featurereport") + "?feature=" + featureId +
                                        "&x=" + String.valueOf(x) + "&y=" + String.valueOf(y) + "&crs=EPSG%3A2056";                                
                                
                                Button pdfBtn = Button.create(Icons.ALL.file_pdf_box_outline_mdi())
                                        .setSize(ButtonSize.SMALL)
                                        .setContent("Objektblatt")
//                                        .setBackground(Color.LIGHT_BLUE)
                                        .setBackground(Color.WHITE)
                                        .elevate(0)
                                        .style()
                                        .setColor("#e53935")
                                        .setBorder("1px #e53935 solid")
                                        .setPadding("5px 5px 5px 0px;")
                                        .setMinWidth(px.of(100)).get();
                                
                                pdfBtn.addClickListener(evt -> {
                                    Window.open(urlReport, "_blank", null);
                                });
                                
                                popupContentBuilder.add(div().style("padding-top: 5px;").element());
                                popupContentBuilder.add(pdfBtn);
                            }
                            popup.appendChild(popupContentBuilder.element());
                        }                        
                    }
                    return null;
                })
                .catch_(error -> {
                    console.log(error);
                    return null;
                });

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