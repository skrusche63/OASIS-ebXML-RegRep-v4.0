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

		QueryRequestContext queryContext = new QueryRequestContext(request);
				
		// Attribute id – The id attribute must be specified by the client to uniquely identify a request. Its 
		// value SHOULD be a UUID URN like “urn:uuid:a2345678-1234-1234-123456789012”.
		
		String requestId = request.getId();

		// initialize query response; as a first step, we make sure, that
		// the request id provided by the incoming request is returned
		QueryResponse queryResponse = createQueryResponse(requestId);
						
		// Attribute federated – This optional attribute specifies that the server must 
		// process this query as a federated query. By default its value is false. 
		
		// This value MUST be false when a server routes a federated query to another 
		// server. This is to avoid an infinite loop in federated query processing.
		
		Boolean federated = request.isFederated();
				
		// as a first step, we distinguish between a federated
		// query request and a request for the local registry
		
		if (federated == true) {
			
			FederatedReadManager rm = FederatedReadManager.getInstance();
			return rm.executeQuery(queryContext, queryResponse);


		} else {
			
			ReadManager rm = ReadManager.getInstance();
			return rm.executeQuery(queryContext, queryResponse);

		}
		
	}
	
	private QueryResponse createQueryResponse(String requestId) {

		QueryResponse queryResponse = ebQueryFactory.createQueryResponse();		
		queryResponse.setRequestId(requestId);
		
		return queryResponse;
	}
}
