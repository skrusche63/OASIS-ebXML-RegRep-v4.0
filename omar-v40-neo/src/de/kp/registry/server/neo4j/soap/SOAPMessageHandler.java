package de.kp.registry.server.neo4j.soap;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.ws.security.WSSecurityException;

import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.common.CredentialInfo;
import de.kp.registry.common.security.SecurityUtilSAML;

public class SOAPMessageHandler implements SOAPHandler<SOAPMessageContext> {

	public void close(MessageContext context) {
		// TODO
	}

	public boolean handleFault(SOAPMessageContext context) {
		// TODO
		return false;
	}

	public boolean handleMessage(SOAPMessageContext wsContext) {

		// this flag is used to distinguish between outgoing
		// and incoming messages
		
		Boolean outboundProperty = (Boolean) wsContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);  
		
        if (outboundProperty.booleanValue()) {  
            System.out.println("Outgoing server message:");  
        
        } else {  

        	// this is an incoming SAML-based SOAP request; the actually
        	// supported protection level is integrity, so we MUST
        	// verify the respective SOAP envelope
    	    SOAPMessage soapMsg  = wsContext.getMessage();
       	    SOAPEnvelope soapEnv;
			
       	    try {
				soapEnv = soapMsg.getSOAPPart().getEnvelope();

				// verify security header and fill respective credential info
	       	    // for later use and credential (assertion) propagation
	            CredentialInfo credentialInfo = new CredentialInfo();
				SecurityUtilSAML.verifySOAPEnvelopeOnServerSAML(soapEnv, credentialInfo);

	            // set scope to make credentialInfo visible for later processing;
	            // this is part of a data sharing mechanism between the soap handler
	            // and subsequent code parts
	            
	    		wsContext.put(CanonicalConstants.CREDENTIAL_INFO, credentialInfo);
	    		wsContext.setScope(CanonicalConstants.CREDENTIAL_INFO, MessageContext.Scope.APPLICATION);      
			
			} catch (SOAPException e) {
				e.printStackTrace();

			} catch (WSSecurityException e) {
				e.printStackTrace();

			}
     
    	  //continue other handler chain
    	  return true;
    	}
        
		return true;
	}

	public Set<QName> getHeaders() {
		// TODO
		return null;
	}

}
