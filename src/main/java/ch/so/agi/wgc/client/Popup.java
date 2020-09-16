package ch.so.agi.wgc.client;

import static elemental2.dom.DomGlobal.console;

import static org.jboss.elemento.Elements.a;
import static org.jboss.elemento.Elements.body;
import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.img;
import static org.jboss.elemento.Elements.span;
import static org.dominokit.domino.ui.style.Unit.px;

import java.util.List;

import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.button.ButtonSize;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.style.Color;
import org.gwtproject.safehtml.shared.SafeHtmlUtils;
import org.jboss.elemento.Attachable;
import org.jboss.elemento.HtmlContentBuilder;
import org.jboss.elemento.IsElement;

import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;

import elemental2.dom.DomGlobal;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.Headers;
import elemental2.dom.Location;
import elemental2.dom.MutationRecord;
import elemental2.dom.RequestInit;
import ol.MapBrowserEvent;

public class Popup implements IsElement<HTMLElement>, Attachable {

    private final HTMLElement root;

    public Popup(WgcMap map, MapBrowserEvent event) {
        HTMLElement icon = Icons.ALL.close().setId("popupCloseIcon").element();
        icon.style.verticalAlign = "middle";
        icon.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(elemental2.dom.Event evt) {
                root.remove();
            }
        });

        HTMLElement closeButton = span().id("popupHeaderButtonSpan").add(icon).element();                    
        HtmlContentBuilder popupBuilder = div().id("popup");
        popupBuilder.add(
                div().id("popupHeader")
                .add(span().id("popupHeaderTextSpan").textContent("Objektinformation"))
                .add(closeButton)
                ); 
        
        root = (HTMLDivElement) popupBuilder.element();
        root.hidden = true;
//        root.style.position = "absolute";
//        root.style.top = "5px";
//        root.style.left = "5px";

        double resolution = map.getView().getResolution();

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
                root.appendChild(div().css("popupNoContent").textContent("Keine weiteren Informationen").element());
            }
                                
            for (int i=0; i<messageDom.getElementsByTagName("Layer").getLength(); i++) {
                Node layerNode = messageDom.getElementsByTagName("Layer").item(i);
                String layerName = ((com.google.gwt.xml.client.Element) layerNode).getAttribute("layername"); 
                String layerTitle = ((com.google.gwt.xml.client.Element) layerNode).getAttribute("name"); 
                
                if (layerNode.getChildNodes().getLength() == 0) {
                    continue;
                };
                
                NodeList htmlNodes = ((com.google.gwt.xml.client.Element) layerNode).getElementsByTagName("HtmlContent");
                for (int j=0; j<htmlNodes.getLength(); j++) {
                                                
                    Text htmlNode = (Text) htmlNodes.item(j).getFirstChild();   
                    root.appendChild(div().css("popupLayerHeader").textContent(layerTitle).element());     
                    
                    HtmlContentBuilder popupContentBuilder = div().css("popupContent");
                    
                    HTMLDivElement featureInfoHtml = div().innerHtml(SafeHtmlUtils.fromTrustedString(htmlNode.getData())).element();
                    popupContentBuilder.add(featureInfoHtml);

                    com.google.gwt.xml.client.Element layerElement = ((com.google.gwt.xml.client.Element) layerNode);
                    if (layerElement.getAttribute("featurereport") != null) {                                
                        double x = event.getCoordinate().getX();
                        double y = event.getCoordinate().getY();
                        
                        com.google.gwt.xml.client.Element featureNode = ((com.google.gwt.xml.client.Element) htmlNodes.item(j).getParentNode());
                        String featureId = featureNode.getAttribute("id");
                        
                        String urlReport = map.getBaseUrlReport() + layerElement.getAttribute("featurereport") + "?feature=" + featureId +
                                "&x=" + String.valueOf(x) + "&y=" + String.valueOf(y) + "&crs=EPSG%3A2056";                                
                        
                        Button pdfBtn = Button.create(Icons.ALL.file_pdf_box_outline_mdi())
                                .setSize(ButtonSize.SMALL)
                                .setContent("Objektblatt")
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
                    root.appendChild(popupContentBuilder.element());
                    root.hidden = false;
                }                        
            }
            return null;
        })
        .catch_(error -> {
            console.log(error);
            DomGlobal.window.alert(error);
            return null;
        });
    }
    
    @Override
    public void attach(MutationRecord mutationRecord) {}

    @Override
    public HTMLElement element() {
        return root;
    }
}
