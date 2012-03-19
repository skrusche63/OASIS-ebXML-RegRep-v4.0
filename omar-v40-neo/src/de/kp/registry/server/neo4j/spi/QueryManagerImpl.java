package de.kp.registry.server.neo4j.spi;

import org.oasis.ebxml.registry.bindings.query.QueryRequest;
import org.oasis.ebxml.registry.bindings.query.QueryResponse;

import de.kp.registry.server.neo4j.federation.FederatedReadManager;
import de.kp.registry.server.neo4j.read.ReadManager;

public class QueryManagerImpl {

	// reference to OASIS ebQuery object factory
	public static org.oasis.ebxml.registry.bindings.query.ObjectFactory ebQueryFactory = new org.oasis.ebxml.registry.bindings.query.ObjectFactory();

	// reference to OASIS ebRIM object factory
	public static org.oasis.ebxml.registry.bindings.rim.ObjectFactory ebRIMFactory = new org.oasis.ebxml.registry.bindings.rim.ObjectFactory();

	public QueryManagerImpl() {
	}
	
	public QueryResponse executeQuery(QueryRequest request) {

		QueryRequestContext queryRequest = new QueryRequestContext(request);
		QueryResponseContext queryResponse = new QueryResponseContext(request.getId());
						
		// Attribute federated – This optional attribute specifies that the server must 
		// process this query as a federated query. By default its value is false. 
		
		// This value MUST be false when a server routes a federated query to another 
		// server. This is to avoid an infinite loop in federated query processing.
		
		Boolean federated = request.isFederated();
				
		// as a first step, we distinguish between a federated
		// query request and a request for the local registry
		
		if (federated == true) {
			
			FederatedReadManager rm = FederatedReadManager.getInstance();
			queryResponse = rm.executeQuery(queryRequest, queryResponse);

			return queryResponse.getQueryResponse();

		} else {
			
			ReadManager rm = ReadManager.getInstance();
			queryResponse = rm.executeQuery(queryRequest, queryResponse);

			return queryResponse.getQueryResponse();

		}
		
	}

}
