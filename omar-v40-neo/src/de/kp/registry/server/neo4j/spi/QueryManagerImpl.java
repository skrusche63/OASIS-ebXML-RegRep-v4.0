package de.kp.registry.server.neo4j.spi;

import org.oasis.ebxml.registry.bindings.query.QueryRequest;
import org.oasis.ebxml.registry.bindings.query.QueryResponse;

import de.kp.registry.server.neo4j.authorization.AuthorizationConstants;
import de.kp.registry.server.neo4j.authorization.AuthorizationHandler;
import de.kp.registry.server.neo4j.authorization.AuthorizationResult;
import de.kp.registry.server.neo4j.federation.FederatedReadManager;
import de.kp.registry.server.neo4j.read.ReadManager;

public class QueryManagerImpl {

	// reference to OASIS ebQuery object factory
	public static org.oasis.ebxml.registry.bindings.query.ObjectFactory ebQueryFactory = new org.oasis.ebxml.registry.bindings.query.ObjectFactory();

	// reference to OASIS ebRIM object factory
	public static org.oasis.ebxml.registry.bindings.rim.ObjectFactory ebRIMFactory = new org.oasis.ebxml.registry.bindings.rim.ObjectFactory();

	// reference to authorization handler
	private static AuthorizationHandler ah = AuthorizationHandler.getInstance();

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

			// Authorization of QueryResponse
			AuthorizationResult authRes = ah.authorizeQueryResponse(queryResponse);
			String result = authRes.getResult();
			
			//__DESIGN__
			
			// in case of a query request, authorization of the outgoing 
			// response is thought of some kind of filter, i.e. no exception
			// is thrown, but only those registry objects are returned, that
			// match the actual authorization policies
			
			if (result.equals(AuthorizationConstants.PERMIT_ALL)) {			

				// in this case, all registry objects or object refs associated 
				// with this request are returned to the client
				
				return queryResponse.getQueryResponse();
				
			} else if (result.equals(AuthorizationConstants.PERMIT_SOME)) {
				
				// this is case, only those registry objects or object refs are
				// returned to the client, that match the authorization policies
				
				// TODO
				
			} else if (result.equals(AuthorizationConstants.PERMIT_NONE)) {
				
				// in this case we have to exclude all registry objects or
				// object refs from the respective result
				queryResponse.clearObjectRef();
				queryResponse.clearRegistryObject();
				
				return queryResponse.getQueryResponse();
				
			}

			return null;

		}
		
	}

}
