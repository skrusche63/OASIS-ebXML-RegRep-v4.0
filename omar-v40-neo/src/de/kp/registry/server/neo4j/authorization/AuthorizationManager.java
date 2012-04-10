package de.kp.registry.server.neo4j.authorization;

import de.kp.registry.server.neo4j.authorization.xacml.PolicyEnforcementPoint;
import de.kp.registry.server.neo4j.service.context.CatalogRequestContext;
import de.kp.registry.server.neo4j.service.context.QueryRequestContext;
import de.kp.registry.server.neo4j.service.context.QueryResponseContext;
import de.kp.registry.server.neo4j.service.context.RemoveRequestContext;
import de.kp.registry.server.neo4j.service.context.SubmitRequestContext;
import de.kp.registry.server.neo4j.service.context.UpdateRequestContext;

public class AuthorizationManager {

	private static AuthorizationManager instance = new AuthorizationManager();
	
	// XACML based policy enforcement point
	private PolicyEnforcementPoint pep = PolicyEnforcementPoint.getInstance();
	
	private AuthorizationManager() {		
	}
	
	public static AuthorizationManager getInstance() {
		if (instance == null) instance = new AuthorizationManager();
		return instance;
	}

	// authorize an incoming CatalogObjectsRequest
	public AuthorizationResult authorizeCatalogRequest(CatalogRequestContext request) {

		AuthorizationResult authRes = new AuthorizationResult(AuthorizationConstants.CATALOG_REQUEST);
		authRes.setUser(request.getUser());
		
		return pep.authorizeRequest(request, authRes);
	
	}
	
	// authorize an incoming SubmitObjectsRequest
	public AuthorizationResult authorizeSubmitRequest(SubmitRequestContext request) {

		AuthorizationResult authRes = new AuthorizationResult(AuthorizationConstants.SUBMIT_REQUEST);
		authRes.setUser(request.getUser());

		return pep.authorizeRequest(request, authRes);
	
	}
	
	// authorize an incoming UpdateObjectsRequest
	public AuthorizationResult authorizeUpdateRequest(UpdateRequestContext request) {

		AuthorizationResult authRes = new AuthorizationResult(AuthorizationConstants.UPDATE_REQUEST);
		authRes.setUser(request.getUser());

		return pep.authorizeRequest(request, authRes);
	
	}
	
	// authorize an incoming RemoveObjectsRequest
	public AuthorizationResult authorizeRemoveRequest(RemoveRequestContext request) {

		AuthorizationResult authRes = new AuthorizationResult(AuthorizationConstants.REMOVE_REQUEST);
		authRes.setUser(request.getUser());

		return pep.authorizeRequest(request, authRes);
	
	}
	
	// authorize an outgoing QueryResponse
	public AuthorizationResult authorizeQueryResponse(QueryRequestContext request, QueryResponseContext response) {

		AuthorizationResult authRes = new AuthorizationResult(AuthorizationConstants.QUERY_REQUEST);
		authRes.setUser(request.getUser());

		return pep.authorizeResponse(response, authRes);
	
	}
}
