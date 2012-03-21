package de.kp.registry.client.service.impl;

import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.oasis.ebxml.registry.bindings.spi.ValidateObjectsRequest;
import org.oasis.ebxml.registry.bindings.spi.ValidateObjectsResponse;

import de.kp.registry.client.security.ConnectionImpl;
import de.kp.registry.client.service.ValidatorSOAPService;
import de.kp.registry.server.neo4j.service.MsgRegistryException;
import de.kp.registry.server.neo4j.service.Validator;

public class ValidatorImpl {

	private static String SAML_USER_ASSERTION = "urn:oasis:names:tc:ebxml-regrep:saml:user:assertion";
	private static QName QNAME = new QName("urn:oasis:names:tc:ebxml-regrep:wsdl:registry:services:4.0", "NotificationListener");
	
	private ValidatorSOAPService service;
	private Validator port;
	
	private ConnectionImpl connection;
	
	public ValidatorImpl(ConnectionImpl connection) {

		// register connection object
		this.connection = connection;
		URL wsdlLocation = this.connection.getLifecyleManagerURL();
		
		service = new ValidatorSOAPService(wsdlLocation, QNAME);
		port = service.getValidatorPort();
		
	}

    public ValidateObjectsResponse validateObjects(ValidateObjectsRequest request) throws MsgRegistryException {

		// TODO:
		
		// assign SAML credentials to request context; this is a mechanism
		// to share the respective assertion with the SOAP message handler
		
		Map<String, Object> context = ((BindingProvider) port).getRequestContext();
		context.put(SAML_USER_ASSERTION, this.connection.getAssertion());

		return port.validateObjects(request);

    }

}
