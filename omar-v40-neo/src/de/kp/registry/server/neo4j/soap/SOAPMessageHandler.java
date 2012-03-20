package de.kp.registry.server.neo4j.soap;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SOAPMessageHandler implements SOAPHandler<SOAPMessageContext> {

	@Override
	public void close(MessageContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext wsContext) {
		// TODO Auto-generated method stub
		
		Boolean outboundProperty = (Boolean) wsContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);  
		
		
        if (outboundProperty.booleanValue()) {  
            System.out.println("Outgoing server message:");  
        } else {  
    		
        	// TODO: xxx pa 120320 this is not working yet!
    		wsContext.put("assertion_server", "server_assertion");
    		
    		// Access to keys from client requestContext is not working
    		System.out.println("--> check client contex assertion?!? " + wsContext.get("assertion"));
    		
    		// default scope is MessageContext.Scope.HANDLER!!!
    		wsContext.setScope("assertion_server", MessageContext.Scope.APPLICATION);      


    		System.out.println("Incoming server message:");  
        }  

		
		return true;
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

}
