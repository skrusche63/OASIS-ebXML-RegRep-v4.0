package de.kp.registry.server.neo4j.soap;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
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
import de.kp.registry.common.security.CertificateUtil;
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

		SOAPMessage soapMsg  = wsContext.getMessage();
   	    SOAPEnvelope soapEnv;
		
        if (outboundProperty.booleanValue()) {  
        	
        	// this is an outgoing SOAP message that is signed and
        	// secured by a Binary Security Token (BST); please
        	// not the asymmetric security handling of incoming
        	// and outgoing SOAP messages

       	    try {
       	    	
				soapEnv = soapMsg.getSOAPPart().getEnvelope();

	            CredentialInfo credentialInfo = new CredentialInfo();
	            CertificateUtil certificateUtil = new CertificateUtil(CanonicalConstants.REGISTRY_OPERATOR);
	            
	            // certificate
	            X509Certificate certificate = certificateUtil.getCertificate();
	            credentialInfo.setCertificate(certificate);
	            
	            // certificate chain
	            Certificate[] certificateChain = certificateUtil.getCertificateChain();
	            credentialInfo.setCertificateChain(certificateChain);
	            
	            // private key
	            PrivateKey privateKey = certificateUtil.getPrivateKey();
	            credentialInfo.setPrivateKey(privateKey);
	            
	            SecurityUtilSAML.signSOAPEnvelopeOnServerBST(soapEnv, credentialInfo);

       	    } catch (SOAPException e) {
				e.printStackTrace();

			}
        
        } else {  

        	// this is an incoming SAML-based SOAP request; the actually
        	// supported protection level is integrity, so we MUST
        	// verify the respective SOAP envelope
			
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

			}
     
    	  //continue other handler chain
    	  return true;
    	}
        
		return true;
	}

	public Set<QName> getHeaders() {
		
		QName securityHeader = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security", "wsse");
			  
		HashSet<QName> headers = new HashSet<QName>();
		headers.add(securityHeader);
		
		return headers;	
		
	}

}
