package de.kp.registry.server.neo4j.federation;

import java.util.concurrent.Callable;

import org.oasis.ebxml.registry.bindings.query.QueryResponse;
import org.oasis.ebxml.registry.bindings.rim.RegistryType;

import de.kp.registry.server.neo4j.service.context.QueryRequestContext;

public class FederationWorker implements Callable<QueryResponse> {
	
	private QueryRequestContext context;
	private RegistryType registry;
	
	public FederationWorker(QueryRequestContext context, RegistryType registry) {

		this.context  = context;
		this.registry = registry;
	
	}

	@Override
	public QueryResponse call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
