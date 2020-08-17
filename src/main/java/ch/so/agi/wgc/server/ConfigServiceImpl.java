package ch.so.agi.wgc.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import ch.so.agi.wgc.shared.BackgroundMapConfig;
import ch.so.agi.wgc.shared.ConfigResponse;
import ch.so.agi.wgc.shared.ConfigService;

public class ConfigServiceImpl extends RemoteServiceServlet implements ConfigService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ApplicationConfig config;

    @Override
    public void init() throws ServletException {
         super.init();
         SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());
    }
    
    @Override
    public ConfigResponse configServer() throws IllegalArgumentException, IOException {
        Enumeration<String> headerNames = request.getHeaderNames();
        for (Enumeration<String> headerName = headerNames; headerName.hasMoreElements();) {
            String header = headerName.nextElement();
            System.out.println(header + " : " + request.getHeader(header));  
        }

        ConfigResponse response = new ConfigResponse();
        response.setBaseUrlWms(config.getBaseUrlWms());
        response.setBaseUrlFeatureInfo(config.getBaseUrlFeatureInfo());
        response.setBaseUrlBigMap(config.getBaseUrlBigMap());
        response.setBaseUrlReport(config.getBaseUrlReport());
        response.setBackgroundMaps(config.getBackgroundMaps());
        return response;
    }
    
    @Override
    protected void checkPermutationStrongName() {
    //http://www.gwtproject.org/javadoc/latest/com/google/gwt/user/server/rpc/RemoteServiceServlet.html    
    }

}
