package de.kp.registry.server.neo4j.service.context;

import de.kp.registry.common.CredentialInfo;

public class RequestContext {

	public String comment;
	public String user;
	
	// CredentialInfo
	public CredentialInfo credentialInfo;
	
	public RequestContext() {	
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return this.comment;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getUser() {
		return this.user;
	}

	public void setCredentialInfo(CredentialInfo credentialInfo) {
		this.credentialInfo = credentialInfo;
	}
	
	public CredentialInfo getCredentailInfo() {
		return this.credentialInfo;
	}
	
}
