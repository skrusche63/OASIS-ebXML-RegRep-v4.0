package de.kp.registry.client.soap;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.common.CredentialInfo;
import de.kp.registry.common.security.SecurityUtilSAML;

public class SOAPMessageHandler implements SOAPHandler<SOAPMessageContext>  {

	public void close(MessageContext context) {
		// TODO
	}

	public boolean handleMessage(SOAPMessageContext wsContext) {
				
		// this flag is used to distinguish between outgoing
		// and incoming messages

		Boolean outboundProperty = (Boolean) wsContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);  

    	SOAPMessage soapMsg  = wsContext.getMessage();
   	    SOAPEnvelope soapEnv;
		 
        if (outboundProperty.booleanValue()) {  
        	
        	// this is an outgoing SAML based SOAP request

       	    try {
       	    	
				soapEnv = soapMsg.getSOAPPart().getEnvelope();

				CredentialInfo credentialInfo = (CredentialInfo)wsContext.get(CanonicalConstants.CREDENTIAL_INFO);    	    
				SecurityUtilSAML.signSOAPEnvelopeOnClientSAML(soapEnv, credentialInfo);
				
			} catch (SOAPException e) {
				e.printStackTrace();
				
			}
        
        } else {  
           	
        	// this is an incoming BST based SOAP request
        	// invoked by a OASIS ebXML RegRep server

        	try {
       	    	
				soapEnv = soapMsg.getSOAPPart().getEnvelope();

	            CredentialInfo credentialInfo = new CredentialInfo();
				SecurityUtilSAML.verifySOAPEnvelopeOnClientBST(soapEnv, credentialInfo);

			} catch (SOAPException e) {
				e.printStackTrace();
				
			}
        
        }  
		
		return true;
		
	}

	public boolean handleFault(SOAPMessageContext context) {
		throw new UnsupportedOperationException("Not supported yet.");
		
//		return false;
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

}
