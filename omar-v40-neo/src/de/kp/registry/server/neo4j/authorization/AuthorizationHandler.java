package de.kp.registry.server.neo4j.authorization;

import de.kp.registry.server.neo4j.spi.RemoveRequestContext;
import de.kp.registry.server.neo4j.spi.SubmitRequestContext;
import de.kp.registry.server.neo4j.spi.UpdateRequestContext;

public class AuthorizationHandler {

	private static AuthorizationHandler instance = new AuthorizationHandler();
	
	private AuthorizationHandler() {		
	}
	
	public static AuthorizationHandler getInstance() {
		if (instance == null) instance = new AuthorizationHandler();
		return instance;
	}
	
	public boolean authorizeSubmitRequest(SubmitRequestContext request) {
		return false;
	}
	
	public boolean authorizeUpdateRequest(UpdateRequestContext request) {
		return false;
	}
	
	public boolean authorizeRemoveRequest(RemoveRequestContext request) {
		return false;
	}
}
