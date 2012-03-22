package de.kp.registry.client.service.impl;

import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.oasis.ebxml.registry.bindings.query.QueryRequest;
import org.oasis.ebxml.registry.bindings.query.QueryResponse;
import de.kp.registry.client.service.QueryManagerSOAPService;
import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.common.ConnectionImpl;
import de.kp.registry.common.CredentialInfo;
import de.kp.registry.server.neo4j.service.MsgRegistryException;
import de.kp.registry.server.neo4j.service.QueryManager;

public class QueryManagerImpl {

	private static QName QNAME = new QName("urn:oasis:names:tc:ebxml-regrep:wsdl:registry:services:4.0", "QueryManager");

	private QueryManagerSOAPService service;
	private QueryManager port;
	
	private ConnectionImpl connection;
	
	public QueryManagerImpl(ConnectionImpl connection) {

		// register connection object
		this.connection = connection;
		URL wsdlLocation = this.connection.getQueryManagerURL();

		service = new QueryManagerSOAPService(wsdlLocation, QNAME);
		port = service.getQueryManagerPort();
		
	}

	public QueryResponse executeQuery(QueryRequest request) throws MsgRegistryException {
		
		// assign SAML credentials to request context; this is a mechanism
		// to share the respective assertion with the SOAP message handler
		
		Map<String, Object> context = ((BindingProvider) port).getRequestContext();
		
		CredentialInfo credentialInfo = new CredentialInfo();
		credentialInfo.setAssertion(this.connection.getAssertion());
		
		context.put(CanonicalConstants.CREDENTIAL_INFO, credentialInfo);
		return port.executeQuery(request);

	}

}
