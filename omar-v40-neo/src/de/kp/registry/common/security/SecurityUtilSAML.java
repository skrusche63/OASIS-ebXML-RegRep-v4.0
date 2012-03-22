package de.kp.registry.common.security;

import java.util.List;

import javax.xml.soap.SOAPEnvelope;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSEncryptionPart;
import org.apache.ws.security.WSSConfig;
import org.apache.ws.security.WSSecurityEngine;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecTimestamp;
import org.apache.ws.security.saml.ext.AssertionWrapper;
import org.apache.ws.security.util.WSSecurityUtil;
import org.opensaml.saml2.core.Assertion;
import org.w3c.dom.Document;

import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.common.CredentialInfo;

public class SecurityUtilSAML extends SecurityUtilBase {
	
    private static Crypto getIssuerCrypto() throws WSSecurityException {
    	return CryptoFactory.getInstance(CanonicalConstants.IDP_CRYPTO);
    }
    private static String getIssuerAlias() {
    	return CanonicalConstants.IDP_ALIAS;
    }
    private static String getIssuerKeypass() {
    	return CanonicalConstants.IDP_KEYPASS;
    }


	/*
	 * This method supports the verification of an incoming
	 * signed SOAP request with a SAML Assertion attached
	 */

    public static void verifySOAPEnvelopeOnServerSAML(SOAPEnvelope soapEnv, CredentialInfo credentialInfo) {
		
		WSSecurityEngine secEngine = new WSSecurityEngine();
        WSSConfig.init();
        
		Document doc = soapEnv.getOwnerDocument();
        
		List<WSSecurityEngineResult> results;
		try {
	
			results = secEngine.processSecurityHeader(doc, null, null, getIssuerCrypto());
			
			WSSecurityEngineResult actionResult = WSSecurityUtil.fetchActionResult(results, WSConstants.ST_UNSIGNED);
	        AssertionWrapper assertionWrapper = (AssertionWrapper) actionResult.get(WSSecurityEngineResult.TAG_SAML_ASSERTION);
	        
	        Assertion assertion = assertionWrapper.getSaml2();                
	        credentialInfo.setAssertion(assertion);
	 
		} catch (WSSecurityException e) {
			e.printStackTrace();
		}
       
	}
	
	/*
	 * This method supports signing of an outgoing request and
	 * in addition the provisoning of a SAML assertion
	 */
    
	public static void signSOAPEnvelopeOnClientSAML(SOAPEnvelope soapEnv, CredentialInfo credentialInfo) {
		try {
			
    		signSOAPEnvelopeSAML(soapEnv, credentialInfo, getIssuerCrypto());
			  
		} catch (WSSecurityException e) {
			e.printStackTrace();
		}
	}

    /*
     * Signing SOAP message with assertion
     */
	protected static void signSOAPEnvelopeSAML(SOAPEnvelope soapEnv, CredentialInfo credentialInfo, Crypto issuerCrypto) throws WSSecurityException {
		
        WSSConfig.init();
		
        // inject empty SecHeader into Document
        Document doc = soapEnv.getOwnerDocument();
        WSSecHeader secHeader = new WSSecHeader();
        
        secHeader.insertSecurityHeader(doc);
        
        WSSecTimestamp timestamp = createTimestamp();
        timestamp.build(doc, secHeader);
        
		SignatureSAML sign = new SignatureSAML();
		
		// <wsse:BinarySecurityToken ...>
		sign.setKeyIdentifierType(WSConstants.BST_DIRECT_REFERENCE);
		
		// <ds:KeyInfo>
		sign.setCertUri(CanonicalConstants.CANONICAL_URI_SENDER_CERT);
		
		String soapNamespace = WSSecurityUtil.getSOAPNamespace(doc.getDocumentElement());

		List<WSEncryptionPart> parts = createReferences(soapNamespace);
		sign.setParts(parts);
		
		AssertionWrapper assertionWrapper = new AssertionWrapper(credentialInfo.getAssertion());

		sign.build(
		        doc, 					// W3C envelope
		        null, 					// uCrypto
		        assertionWrapper,  		// assertion
		        issuerCrypto, 			// iCrypto
		        getIssuerAlias(), 		// iKeyName
		        getIssuerKeypass(), 	// iKeyPW
		        secHeader				// secHeader
		    );

	}
	
}
