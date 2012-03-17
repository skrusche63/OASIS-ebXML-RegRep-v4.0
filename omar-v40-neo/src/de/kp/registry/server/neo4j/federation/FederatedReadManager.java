package de.kp.registry.server.neo4j.federation;

import org.oasis.ebxml.registry.bindings.query.QueryResponse;

import de.kp.registry.server.neo4j.spi.QueryRequestContext;

public class FederatedReadManager {

	private static FederatedReadManager instance = new FederatedReadManager();
	
	private FederatedReadManager() {		
	}
	
	public static FederatedReadManager getInstance() {
		if (instance == null) instance = new FederatedReadManager();
		return instance;
	}
	
 	public QueryResponse executeQuery(QueryRequestContext queryContext, QueryResponse queryResponse) { 		
 		
 		// The FederationManager MUST invoke the OASIS ebXML RegRep v4.0 WS Client functionality
 		return queryResponse; 		
 	}

}
