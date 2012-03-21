package de.kp.registry.server.neo4j.federation;

import de.kp.registry.server.neo4j.service.context.QueryRequestContext;
import de.kp.registry.server.neo4j.service.context.QueryResponseContext;

public class FederatedReadManager {

	private static FederatedReadManager instance = new FederatedReadManager();
	
	private FederatedReadManager() {		
	}
	
	public static FederatedReadManager getInstance() {
		if (instance == null) instance = new FederatedReadManager();
		return instance;
	}
	
 	public QueryResponseContext executeQuery(QueryRequestContext queryRequest, QueryResponseContext queryResponse) { 		
 		
 		// The FederationManager MUST invoke the OASIS ebXML RegRep v4.0 WS Client functionality
 		return queryResponse; 		
 	}

}
