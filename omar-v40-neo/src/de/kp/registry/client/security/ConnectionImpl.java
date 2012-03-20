package de.kp.registry.client.security;

import java.net.MalformedURLException;
import java.net.URL;

import org.opensaml.saml2.core.Assertion;

public class ConnectionImpl {

	private Assertion assertion;
	private String registryURL;
	
	// postfix of the respective WSDL locations
	private static String LCM_WSDL 	 = "/lcm?wsdl";
	private static String QUERY_WSDL = "/query?wsdl";
	
	public ConnectionImpl() {
	}
	
	public void setAssertion(Assertion assertion) {
		this.assertion = assertion;
	}
	
	public Assertion getAssertion() {
		return this.assertion;
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
