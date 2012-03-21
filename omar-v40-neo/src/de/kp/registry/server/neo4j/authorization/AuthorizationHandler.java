package de.kp.registry.server.neo4j.authorization;

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
	
	// authorize an incoming SubmitObjectsRequest
	public AuthorizationResult authorizeSubmitRequest(SubmitRequestContext request) {
		// TODO
		return null;
	}
	
	// authorize an incoming UpdateObjectsRequest
	public AuthorizationResult authorizeUpdateRequest(UpdateRequestContext request) {
		// TODO
		return null;
	}
	
	// authorize an incoming RemoveObjectsRequest
	public AuthorizationResult authorizeRemoveRequest(RemoveRequestContext request) {
		// TODO
		return null;
	}
	
	// authorize an outgoing QueryResponse
	public AuthorizationResult authorizeQueryResponse(QueryResponseContext response) {
		// TODO
		return null;
	}
}
