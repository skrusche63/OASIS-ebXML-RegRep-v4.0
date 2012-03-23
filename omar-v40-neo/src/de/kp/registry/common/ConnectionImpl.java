package de.kp.registry.common;

import java.net.MalformedURLException;
import java.net.URL;

import org.opensaml.saml2.core.Assertion;

public class ConnectionImpl {

	private CredentialInfo credentialInfo;
	private String registryURL;
	
	// postfix of the respective WSDL locations
	private static String LCM_WSDL 	 = "/lcm?wsdl";
	private static String QUERY_WSDL = "/query?wsdl";
	
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
	
	public URL getQueryManagerURL() {
		
		URL endpoint = null;
		
		try {
			endpoint = new URL(registryURL + QUERY_WSDL);
		
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return endpoint;
		
	}

	public URL getLifecyleManagerURL() {
		
		URL endpoint = null;
		
		try {
			endpoint = new URL(registryURL + LCM_WSDL);
		
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return endpoint;
		
	}

}
