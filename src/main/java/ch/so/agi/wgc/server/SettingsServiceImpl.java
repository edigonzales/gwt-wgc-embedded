package ch.so.agi.wgc.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import ch.so.agi.wgc.shared.ApplicationConfig;
import ch.so.agi.wgc.shared.BackgroundMap;
import ch.so.agi.wgc.shared.SettingsResponse;
import ch.so.agi.wgc.shared.SettingsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsServiceImpl extends RemoteServiceServlet implements SettingsService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

//    @Value("${app.myVar}")
//    private String myVar;

    @Autowired
    private ApplicationConfig config;

    @Override
    public void init() throws ServletException {
         super.init();
         SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());
    }
    
    @Override
    public SettingsResponse settingsServer() throws IllegalArgumentException, IOException {
        HashMap<String,Object> settings = new HashMap<String,Object>();
        
//        settings.put("MY_VAR", myVar);
        
//        settings.put("CONFIG", config.getBackgroundMaps());
        
        BackgroundMap foo = new BackgroundMap();
        foo.setId("arsch");
        
        settings.put("CONFIG", foo);

        SettingsResponse response = new SettingsResponse();
        response.setSettings(settings);
        
        
        
        
        return response;
    }
}
