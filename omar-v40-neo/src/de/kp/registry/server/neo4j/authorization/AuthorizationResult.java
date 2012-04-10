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
	private String result;
	
	private String user;
	
	public AuthorizationResult(String requestType) {
		
		this.requestType = requestType;
		
		this.authorizedResources = new HashSet<String>();
		this.deniedResources = new HashSet<String>();
		
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public String getResult() {
		
		if (this.result != null) return this.result;
		
		// compute authorization result from the list of 
		// resources that have been authorized or even not
		
		if (this.deniedResources.isEmpty()) return AuthorizationConstants.PERMIT_ALL;
		
		// some or all resources have been denied
		if (this.authorizedResources.isEmpty()) return AuthorizationConstants.PERMIT_NONE;
		
		return AuthorizationConstants.PERMIT_SOME;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getUser() {
		return this.user;
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
	
	public void addAuthorized(String resource) {
		this.authorizedResources.add(resource);
	}
	
	public void addDenied(String resource) {
		this.deniedResources.add(resource);
	}
}
