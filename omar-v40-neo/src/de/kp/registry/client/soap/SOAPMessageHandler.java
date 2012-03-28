package de.kp.registry.client.soap;

import java.io.PrintStream;
import java.util.HashSet;
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

	// This is called after the completion of message processing by all handlers for each web service 
	// invocation (after completion of MEP). This can be useful to clean up any resources used during 
	// processing the message.
	
	public void close(MessageContext context) {
		// do nothing to clean up
	}

	// This is called for inbound and outbound messages.
	
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

	// This is called instead of handleMessage(), when the message contains a protocol fault.
	
	public boolean handleFault(SOAPMessageContext wsContext) {
		
		PrintStream outstream = System.out;
		
		// indicate that there is a protocol fault for the current 
		// SOAP message

		outstream.println("[SOAPHandler] Exception in Client-side SOAPHandler: ");
		SOAPMessage soapMsg = wsContext.getMessage();

		try {
			
			outstream.println("--->");
			soapMsg.writeTo(outstream);
			
	        outstream.println("<---");
	      
		} catch (Exception e) {
	         outstream.println("Unable to write exception for protocol fault: " + e.toString());
		}
	      
		return true;

	}

	public Set<QName> getHeaders() {

		QName securityHeader = new QName(CanonicalConstants.WSSE_NS, "Security", "wsse");
		  
		HashSet<QName> headers = new HashSet<QName>();
		headers.add(securityHeader);
		
		return headers;	
		
	}

}
