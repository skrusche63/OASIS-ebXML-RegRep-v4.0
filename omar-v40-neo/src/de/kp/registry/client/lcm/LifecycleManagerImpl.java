package de.kp.registry.client.lcm;

import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.oasis.ebxml.registry.bindings.lcm.RemoveObjectsRequest;
import org.oasis.ebxml.registry.bindings.lcm.SubmitObjectsRequest;
import org.oasis.ebxml.registry.bindings.lcm.UpdateObjectsRequest;
import org.oasis.ebxml.registry.bindings.rs.RegistryResponseType;

import de.kp.registry.client.LifecycleManagerSOAPService;
import de.kp.registry.client.security.ConnectionImpl;
import de.kp.registry.server.neo4j.service.LifecycleManager;
import de.kp.registry.server.neo4j.service.MsgRegistryException;

public class LifecycleManagerImpl {

	private static String SAML_USER_ASSERTION = "urn:oasis:names:tc:ebxml-regrep:saml:user:assertion";
	private static QName QNAME = new QName("urn:oasis:names:tc:ebxml-regrep:wsdl:registry:services:4.0", "LifecycleManager");

	private LifecycleManagerSOAPService lcm;
	private LifecycleManager port;
	
	private ConnectionImpl connection;
	
	public LifecycleManagerImpl(ConnectionImpl connection) {

		// register connection object
		this.connection = connection;
		URL wsdlLocation = this.connection.getLifecyleManagerURL();
		
		lcm = new LifecycleManagerSOAPService(wsdlLocation, QNAME);
		port = lcm.getLifecycleManagerPort();
	}
	
	public RegistryResponseType removeObjects(RemoveObjectsRequest request) throws MsgRegistryException {

		// TODO:
		
		// assign SAML credentials to request context; this is a mechanism
		// to share the respective assertion with the SOAP message handler
		
		Map<String, Object> context = ((BindingProvider) port).getRequestContext();
		context.put(SAML_USER_ASSERTION, this.connection.getAssertion());

		return port.removeObjects(request);

	}

	public RegistryResponseType submitObjects(SubmitObjectsRequest request) throws MsgRegistryException {

		// TODO:
		
		// assign SAML credentials to request context; this is a mechanism
		// to share the respective assertion with the SOAP message handler
		
		Map<String, Object> context = ((BindingProvider) port).getRequestContext();
		context.put(SAML_USER_ASSERTION, this.connection.getAssertion());

		return port.submitObjects(request);

	}
	
	public RegistryResponseType updateObjects(UpdateObjectsRequest request) throws MsgRegistryException {

		// TODO:
		
		// assign SAML credentials to request context; this is a mechanism
		// to share the respective assertion with the SOAP message handler
		
		Map<String, Object> context = ((BindingProvider) port).getRequestContext();
		context.put(SAML_USER_ASSERTION, this.connection.getAssertion());

		return port.updateObjects(request);

	}
	
}
