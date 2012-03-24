package de.kp.registry.server.neo4j.authorization;

import java.util.HashSet;
import java.util.Set;

public class AuthorizationResult {

	// this is a temporary cache to hold the unique
	// identifiers of all the resources that have be
	// accepted by authorization	
	private Set<String> authorizedResources;
	
	// this is a temporary cache to hold the unique
	// identifiers of all the resources that have been
	// denied by authorization	
	private Set<String> deniedResources;
	
	private String requestType;
	
	public AuthorizationResult(String requestType) {
		
		this.requestType = requestType;
		
		this.authorizedResources = new HashSet<String>();
		this.deniedResources = new HashSet<String>();
		
	}
	
	public String getResult() {
		
		if (this.deniedResources.isEmpty()) return AuthorizationConstants.PERMIT_ALL;
		
		// some or all resources have been denied
		if (this.authorizedResources.isEmpty()) return AuthorizationConstants.PERMIT_NONE;
		
		return AuthorizationConstants.PERMIT_SOME;
	}
	
	public Set<String> getAuthorized() {
		return this.authorizedResources;
	}
	
	public Set<String> getDenied() {
		return this.deniedResources;
	}
	
	public String getRequestType() {
		return this.requestType;
	}
}
