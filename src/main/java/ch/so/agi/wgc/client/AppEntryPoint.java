package ch.so.agi.wgc.client;

import static elemental2.dom.DomGlobal.console;
import static org.jboss.elemento.Elements.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.dominokit.domino.ui.dropdown.DropDownMenu;
import org.dominokit.domino.ui.forms.SuggestBoxStore;
import org.dominokit.domino.ui.forms.LocalSuggestBoxStore;
import org.dominokit.domino.ui.forms.SuggestBox;
import org.dominokit.domino.ui.forms.SuggestItem;
import org.dominokit.domino.ui.forms.SuggestBox.DropDownPositionDown;
import org.dominokit.domino.ui.forms.SuggestBoxStore.SuggestionsHandler;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.style.ColorScheme;
import org.dominokit.domino.ui.tabs.Tab;
import org.dominokit.domino.ui.tabs.TabsPanel;
import org.dominokit.domino.ui.themes.Theme;
import org.dominokit.domino.ui.utils.DelayedTextInput;
import org.dominokit.domino.ui.utils.DelayedTextInput.DelayedAction;
import org.dominokit.domino.ui.utils.DominoElement;
import org.dominokit.domino.ui.utils.HasChangeHandlers.ChangeHandler;
import org.dominokit.domino.ui.utils.HasSelectionHandler.SelectionHandler;
import org.jboss.elemento.EventType;

//import org.dominokit.domino.ui.Typography.Paragraph;
//import org.dominokit.domino.ui.cards.Card;
//import org.dominokit.domino.ui.forms.LocalSuggestBoxStore;
//import org.dominokit.domino.ui.forms.SuggestBoxStore;
//import org.dominokit.domino.ui.forms.SuggestItem;
////import org.dominokit.domino.ui.forms.SuggestBox;
//import org.dominokit.domino.ui.grid.Column;
//import org.dominokit.domino.ui.grid.Row;
//import org.dominokit.domino.ui.grid.Row_12;
//import org.dominokit.domino.ui.style.Color;
//import org.dominokit.domino.ui.tabs.Tab;
//import org.dominokit.domino.ui.tabs.TabsPanel;
//import org.dominokit.domino.ui.themes.Theme;
//import org.dominokit.domino.ui.cards.Card;
//import org.dominokit.domino.ui.utils.TextNode;
//import org.jboss.gwt.elemento.core.Elements;
//import static org.jboss.gwt.elemento.core.Elements.b;
//import static org.jboss.gwt.elemento.core.Elements.div;
//import org.dominokit.domino.ui.utils.TextNode;
//import org.jboss.elemento.Elements;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import ch.qos.logback.classic.Logger;
import ch.so.agi.wgc.shared.BackgroundMap;
import ch.so.agi.wgc.shared.ConfigResponse;
import ch.so.agi.wgc.shared.ConfigService;
import ch.so.agi.wgc.shared.ConfigServiceAsync;
import ch.so.agi.wgc.shared.SettingsResponse;
import ch.so.agi.wgc.shared.SettingsService;
import ch.so.agi.wgc.shared.SettingsServiceAsync;
//import elemental2.core.Global;
//import elemental2.core.JsArray;
//import elemental2.dom.DomGlobal;
//import elemental2.dom.HTMLElement;
//import elemental2.dom.HTMLDivElement;
//import elemental2.dom.HTMLElement;
//import elemental2.dom.Response;
import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.core.JsMap;
import elemental2.core.JsNumber;
import elemental2.core.JsObject;
import elemental2.core.JsString;
import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.Headers;
import elemental2.dom.KeyboardEvent;
import elemental2.dom.RequestInit;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import ol.Coordinate;
import ol.Extent;

//import jsinterop.base.Js;
//import jsinterop.base.JsPropertyMap;

public class AppEntryPoint implements EntryPoint {
    private MyMessages messages = GWT.create(MyMessages.class);
//    private final SettingsServiceAsync settingsService = GWT.create(SettingsService.class);
    private final ConfigServiceAsync configService = GWT.create(ConfigService.class);
    
    private String MY_VAR;

    private NumberFormat fmtDefault = NumberFormat.getDecimalFormat();
    private NumberFormat fmtPercent = NumberFormat.getFormat("#0.0");
    
    private String MAP_DIV_ID = "map";
    
    public void onModuleLoad() {
        configService.configServer(new AsyncCallback<ConfigResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                console.log("fail");
                
            }

            @Override
            public void onSuccess(ConfigResponse result) {
                console.log("success");
                List<BackgroundMap> backgroundMaps = result.getBackgroundMaps();                
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
        // new URLSearchParams() not available in RC1
        if (Window.Location.getParameter("egrid") != null) {
            // getParameterMap()
            // String egrid = Window.Location.getParameter("egrid").toString();
        }
        
        String bgLayer = "";
        if (Window.Location.getParameter("bgLayer") != null) {
            bgLayer = Window.Location.getParameter("bgLayer").toString();
            console.log("bgLayer: ", bgLayer);            
        } else {
            DomGlobal.window.alert("bgLayer missing");
            console.error("bgLayer missing");
            return;
        }


        
        
//        java.util.Map<String,List<String>> paramsMap = Window.Location.getParameterMap();
//        console.log(paramsMap.toString());
        
    }

   private static native void updateURLWithoutReloading(String newUrl) /*-{
        $wnd.history.pushState(newUrl, "", newUrl);
    }-*/;
}