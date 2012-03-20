package de.kp.registry.client.soap;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SOAPMessageHandler implements SOAPHandler<SOAPMessageContext>  {

	@Override
	public void close(MessageContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		// TODO Auto-generated method stub
				
		Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);  
		 
        if (outboundProperty.booleanValue()) {  
            System.out.println("Outgoing client message:");  
        } else {  
            System.out.println("Incoming client message:");  
        }  
        
        
		//Assertion ASSERTION = (Assertion) context.get("assertion");
        
		//System.out.println("---> assertion: " + ASSERTION);
		
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		// TODO Auto-generated method stub
		
		throw new UnsupportedOperationException("Not supported yet.");
		
//		return false;
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

}
