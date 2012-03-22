package de.kp.registry.common.security;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
import org.apache.ws.security.util.WSSecurityUtil;

import org.w3c.dom.Document;

import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.common.CredentialInfo;

public class SecurityUtilBase {

    private final static Logger logger = Logger.getLogger(SecurityUtilBase.class.getName());

	/* 
	 *  The assumed maximum skew (milliseconds) between the local times 
	 *  of any two systems.
	 */
    
	private static String getKeyMaxClockSkew() {
		return CanonicalConstants.MAX_CLOCK_SKEW;
	}

	/*
	 * Convenience method to extract an UUID/URN from a Content ID (CID). CIDs
	 * are used for identifying attachments in signed SOAP messages.
	 */
	
	public static String convertCIDToUUID(String cid) throws Exception {

		if (!(cid.charAt(0) == '<' && cid.charAt(cid.length() - 1) == '>')) {
			throw new Exception("Content ID with URI Scheme expected, but '" + cid + "' retrieved.");
		}
	
		String uuid = cid.substring(1, cid.length() - 1);
		return uuid;
	}

	/*
	 * Convenience method to turn an UUID/URN into a Content ID (CID). CIDs are
	 * used for identifying attachments in signed SOAP messages.
	 */
	
	public static String convertUUIDToCID(String uuid) {
		String cid = "<" + uuid + ">";
		return cid;
	}

	protected static List<WSEncryptionPart> createReferences(String soapNamespace) {

		List<WSEncryptionPart> parts = new ArrayList<WSEncryptionPart>();
        
		WSEncryptionPart encP1 = new WSEncryptionPart(WSConstants.TIMESTAMP_TOKEN_LN, WSConstants.WSU_NS, "Element");
        parts.add(encP1);
        
        WSEncryptionPart encP2 = new WSEncryptionPart(WSConstants.ELEM_BODY, soapNamespace, "Content");
        parts.add(encP2);

        return parts;
	
	}


	protected static WSSecTimestamp createTimestamp() {
        
        WSSecTimestamp timestamp = new WSSecTimestamp();
        
        int timeToLive = 300; // default
        String maxClockSkew = getKeyMaxClockSkew();
        
        if (maxClockSkew != null) {
            try {
            	timeToLive = Integer.parseInt(maxClockSkew);

            } catch (NumberFormatException e) {
                logger.warning(e.getMessage());
            }
        }
        
        timestamp.setTimeToLive(timeToLive);
		return timestamp;
	}
	
	
	/*
	 * This method supports an OASIS ebXML RegRep client, when receiving
	 * a signed SOAP message from the respective server; note, that in
	 * this use case, the server uses a Binary Security Token (BST)
	 */

	public static void verifySOAPEnvelopeOnClientBST(SOAPEnvelope soapEnv, CredentialInfo credentialInfo) {

		try {

			Crypto crypto = CryptoFactory.getInstance("crypto-client.properties");
			verifySOAPEnvelopeBST(soapEnv, credentialInfo, crypto);
		
		} catch (WSSecurityException e) {
			e.printStackTrace();

		}

	}
	
	/*
	 * This method support an OASIS ebXML RegRep server to sign an
	 * outgoing SOAP message with a pre-configured server certificate
	 */

	public static void signSOAPEnvelopeOnServerBST(SOAPEnvelope soapEnv, CredentialInfo credentialInfo) {
		
		try {
			signSOAPEnvelopeBST(soapEnv, credentialInfo, CryptoFactory.getInstance("crypto-server.properties"));
		
		} catch (WSSecurityException e) {
			e.printStackTrace();
		}

	}
	
	/*
	 * This method verifies a signed SOAP message with a Binary Security
	 * Token (BST) provided; this is a helper, that is actually used on
	 * the client side only (when receiving server responses)
	 */
	
	protected static void verifySOAPEnvelopeBST(SOAPEnvelope soapEnv, CredentialInfo credentialInfo, Crypto crypto) throws WSSecurityException {
		
		WSSecurityEngine secEngine = new WSSecurityEngine();		
	    WSSConfig.init();
		
		Document doc = soapEnv.getOwnerDocument();
	    
		List<WSSecurityEngineResult> results = secEngine.processSecurityHeader(doc, null, null, crypto);
	
		if (results != null) {
			    				
	        WSSecurityEngineResult actionResult = WSSecurityUtil.fetchActionResult(results, WSConstants.SIGN);
	        X509Certificate certificate = (X509Certificate)actionResult.get(WSSecurityEngineResult.TAG_X509_CERTIFICATE);
			
			// inject certificate for further processing
	        credentialInfo.setCertificate(certificate);
	        
		}
	}
	
    /*
     * This method signs an outgoing SOAP message with
     * a digital certificate
     */
	
	protected static void signSOAPEnvelopeBST(SOAPEnvelope soapEnv, CredentialInfo credentialInfo, Crypto userCrypto) throws WSSecurityException {
		
		WSSConfig.init();

		// inject empty SecHeader into Document
		Document doc = soapEnv.getOwnerDocument();
		
		WSSecHeader secHeader = new WSSecHeader();
		secHeader.insertSecurityHeader(doc);

		WSSecTimestamp timestamp = createTimestamp();
		timestamp.build(doc, secHeader);

		// overridden WSS4J path for explicit setPrivateKey()
		// <ds:Signature>
		SignatureBST sign = new SignatureBST();

		// <wsse:BinarySecurityToken ...>
		sign.setX509Certificate(credentialInfo.getCertificate());
		sign.setKeyIdentifierType(WSConstants.BST_DIRECT_REFERENCE);

		sign.setPrivateKey(credentialInfo.getPrivateKey());

		// <ds:KeyInfo>
		sign.setCertUri(CanonicalConstants.CANONICAL_URI_SENDER_CERT);

		String soapNamespace = WSSecurityUtil.getSOAPNamespace(doc.getDocumentElement());

		// signature references
		// <ds:Reference>
		List<WSEncryptionPart> parts = createReferences(soapNamespace);
		sign.setParts(parts);

		sign.build(doc, userCrypto, secHeader);
		
	}
	
}