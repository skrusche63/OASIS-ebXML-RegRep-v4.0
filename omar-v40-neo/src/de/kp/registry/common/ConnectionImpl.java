package de.kp.registry.common;

import java.net.MalformedURLException;
import java.net.URL;

import org.opensaml.saml2.core.Assertion;

public class ConnectionImpl {

	private CredentialInfo credentialInfo;
	private String registryURL;
	
	public ConnectionImpl() {
	}
	
	public void setCredentialInfo(CredentialInfo credentialInfo) {
		this.credentialInfo = credentialInfo;
	}
	
	public Assertion getAssertion() {
		
		if (this.credentialInfo == null) return null;
		return this.credentialInfo.getAssertion();
		
	}
	
	public void setRegistryUrl(String registryURL) {
		this.registryURL = registryURL;
	}

	public URL getCatalogerURL() {
		return getEndpoint(CanonicalConstants.CATALOG_WSDL);
	}

	public URL getQueryManagerURL() {
		return getEndpoint(CanonicalConstants.QUERY_WSDL);
	}

	public URL getLifecyleManagerURL() {
		return getEndpoint(CanonicalConstants.LIFECYCLE_WSDL);
	}

	public URL getNotificationListenerURL() {
		return getEndpoint(CanonicalConstants.NOTIFICATION_WSDL);
	}

	private URL getEndpoint(String relativeURL) {

		URL endpoint = null;
		
		try {
			endpoint = new URL(registryURL + relativeURL);
		
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return endpoint;

	}
}
