package de.kp.registry.client.qm;

import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.oasis.ebxml.registry.bindings.query.QueryRequest;
import org.oasis.ebxml.registry.bindings.query.QueryResponse;
import de.kp.registry.client.QueryManagerSOAPService;
import de.kp.registry.client.security.ConnectionImpl;
import de.kp.registry.server.neo4j.service.MsgRegistryException;
import de.kp.registry.server.neo4j.service.QueryManager;

public class QueryManagerImpl {

	private static String SAML_USER_ASSERTION = "urn:oasis:names:tc:ebxml-regrep:saml:user:assertion";
	private static QName QNAME = new QName("urn:oasis:names:tc:ebxml-regrep:wsdl:registry:services:4.0", "QueryManager");

	private QueryManagerSOAPService lcm;
	private QueryManager port;
	
	private ConnectionImpl connection;
	
	public QueryManagerImpl(ConnectionImpl connection) {

		// register connection object
		this.connection = connection;
		URL wsdlLocation = this.connection.getQueryManagerURL();

		lcm = new QueryManagerSOAPService(wsdlLocation, QNAME);
		port = lcm.getQueryManagerPort();
		
	}

	public QueryResponse executeQuery(QueryRequest request) throws MsgRegistryException {

		// TODO:
		
		// assign SAML credentials to request context; this is a mechanism
		// to share the respective assertion with the SOAP message handler
		
		Map<String, Object> context = ((BindingProvider) port).getRequestContext();
		context.put(SAML_USER_ASSERTION, this.connection.getAssertion());

		return port.executeQuery(request);

	}

}
