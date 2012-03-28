package de.kp.registry.server.neo4j.federation;

import java.util.concurrent.Callable;

import org.oasis.ebxml.registry.bindings.query.QueryRequest;
import org.oasis.ebxml.registry.bindings.query.QueryResponse;
import org.oasis.ebxml.registry.bindings.rim.RegistryType;

import de.kp.registry.client.service.impl.QueryManagerImpl;
import de.kp.registry.common.ConnectionImpl;
import de.kp.registry.common.CredentialInfo;
import de.kp.registry.server.neo4j.service.MsgRegistryException;
import de.kp.registry.server.neo4j.service.context.QueryRequestContext;

public class FederationWorker implements Callable<QueryResponse> {
	
	private QueryRequestContext request;
	private RegistryType registry;
	
	public FederationWorker(QueryRequestContext request, RegistryType registry) {

		this.request  = request;
		this.registry = registry;
	
	}

	// this method queries a remote federate registry with the
	// initial request the current registry is queried with
	
	public QueryResponse call() throws Exception {

	   	CredentialInfo credentialInfo = this.request.getCredentialInfo();
    	if (credentialInfo == null) return null;

		QueryRequest remoteRequest = this.request.getQueryRequest();
		QueryResponse remoteResponse = null;
		
		try {
			
			ConnectionImpl remoteConnection = new ConnectionImpl();

			remoteConnection.setRegistryUrl(this.registry.getBaseURL());
			remoteConnection.setCredentialInfo(credentialInfo);

			// invoke client side query manager to retrieve the respective
			// remote response from the federate registry instance
			
			QueryManagerImpl qm = new QueryManagerImpl(remoteConnection);
			remoteResponse = qm.executeQuery(remoteRequest);

		} catch (MsgRegistryException e) {
			e.printStackTrace();
		}

		return remoteResponse;
		
	}
	
}
