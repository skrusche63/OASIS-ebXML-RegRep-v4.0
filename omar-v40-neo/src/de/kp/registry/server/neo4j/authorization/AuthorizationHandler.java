package de.kp.registry.server.neo4j.authorization;

import de.kp.registry.server.neo4j.service.context.CatalogRequestContext;
import de.kp.registry.server.neo4j.service.context.QueryResponseContext;
import de.kp.registry.server.neo4j.service.context.RemoveRequestContext;
import de.kp.registry.server.neo4j.service.context.SubmitRequestContext;
import de.kp.registry.server.neo4j.service.context.UpdateRequestContext;

public class AuthorizationHandler {

	private static AuthorizationHandler instance = new AuthorizationHandler();
	
	private AuthorizationHandler() {		
	}
	
	public static AuthorizationHandler getInstance() {
		if (instance == null) instance = new AuthorizationHandler();
		return instance;
	}

	// authorize an incoming CatalogObjectsRequest
	public AuthorizationResult authorizeCatalogRequest(CatalogRequestContext request) {

		AuthorizationResult authRes = new AuthorizationResult(AuthorizationConstants.CATALOG_REQUEST);
		// TODO
		return authRes;
	
	}
	
	// authorize an incoming SubmitObjectsRequest
	public AuthorizationResult authorizeSubmitRequest(SubmitRequestContext request) {

		AuthorizationResult authRes = new AuthorizationResult(AuthorizationConstants.SUBMIT_REQUEST);
		// TODO
		return authRes;
	
	}
	
	// authorize an incoming UpdateObjectsRequest
	public AuthorizationResult authorizeUpdateRequest(UpdateRequestContext request) {

		AuthorizationResult authRes = new AuthorizationResult(AuthorizationConstants.UPDATE_REQUEST);
		// TODO
		return authRes;
	
	}
	
	// authorize an incoming RemoveObjectsRequest
	public AuthorizationResult authorizeRemoveRequest(RemoveRequestContext request) {

		AuthorizationResult authRes = new AuthorizationResult(AuthorizationConstants.REMOVE_REQUEST);
		// TODO
		return authRes;
	
	}
	
	// authorize an outgoing QueryResponse
	public AuthorizationResult authorizeQueryResponse(QueryResponseContext response) {

		AuthorizationResult authRes = new AuthorizationResult(AuthorizationConstants.QUERY_REQUEST);
		// TODO
		return authRes;
	
	}
}
