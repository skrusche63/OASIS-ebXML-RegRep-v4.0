package de.kp.registry.server.neo4j.common;

import org.opensaml.saml2.core.Assertion;

public class RequestContext {

	public String comment;
	public String user;
	
	// SAML assertion
	public Assertion assertion;
	
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

	public void setAssertion(Assertion assertion) {
		this.assertion = assertion;
	}
	
	public Assertion getAssertion() {
		return this.assertion;
	}
	
}
