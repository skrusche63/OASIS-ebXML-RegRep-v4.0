package de.kp.registry.client.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import de.kp.registry.client.soap.SOAPMessageHandler;
import de.kp.registry.server.neo4j.service.NotificationListener;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "NotificationListenerSOAPService", targetNamespace = "urn:oasis:names:tc:ebxml-regrep:wsdl:registry:services:4.0", 
wsdlLocation = "WEB-INF/wsdl/regrep-server-service.wsdl")

public class NotificationListenerSOAPService extends Service {

    private final static URL WS_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(de.kp.registry.client.service.NotificationListenerSOAPService.class.getName());

    private final static String TARGET_NAMESPACE = "urn:oasis:names:tc:ebxml-regrep:wsdl:registry:services:4.0";

    static {
    	
        URL url = null;       
        try {
        	
            URL baseUrl = de.kp.registry.client.service.NotificationListenerSOAPService.class.getResource(".");
            url = new URL(baseUrl, "WEB-INF/wsdl/regrep-server-service.wsdl");
        
        } catch (MalformedURLException e) {
        	
            logger.warning("Failed to create URL for the wsdl Location: 'WEB-INF/wsdl/regrep-server-service.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        
        WS_WSDL_LOCATION = url;
    
    }

    public NotificationListenerSOAPService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
        
        this.setHandlerResolver(new HandlerResolver() {
	
		 	@SuppressWarnings("rawtypes")
				public List<Handler> getHandlerChain(PortInfo portInfo) {
		         
		 		List<Handler> handlerList = new ArrayList<Handler>();
		         handlerList.add(new SOAPMessageHandler());
		         
		         return handlerList;
		     }
	 
        });

    }

    public NotificationListenerSOAPService() {
        super(WS_WSDL_LOCATION, new QName(TARGET_NAMESPACE, "NotificationListenerSOAPService"));
    }

    /**
     * 
     * @return
     *     returns NotificationListener
     */
    @WebEndpoint(name = "NotificationListenerPort")
    public NotificationListener getNotificationListenerPort() {
        return super.getPort(new QName(TARGET_NAMESPACE, "NotificationListenerPort"), NotificationListener.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns NotificationListener
     */
    @WebEndpoint(name = "NotificationListenerPort")
    public NotificationListener getNotificationListenerPort(WebServiceFeature... features) {
        return super.getPort(new QName(TARGET_NAMESPACE, "NotificationListenerPort"), NotificationListener.class, features);
    }

}
