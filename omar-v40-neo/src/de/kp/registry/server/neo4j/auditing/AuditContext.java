package de.kp.registry.server.neo4j.auditing;

import org.oasis.ebxml.registry.bindings.rim.ActionType;
import org.oasis.ebxml.registry.bindings.rim.AuditableEventType;
import org.oasis.ebxml.registry.bindings.rs.RegistryResponseType;

public class AuditContext {

	// reference to OASIS ebRIM object factory
	public static org.oasis.ebxml.registry.bindings.rim.ObjectFactory ebRIMFactory = new org.oasis.ebxml.registry.bindings.rim.ObjectFactory();

	private RegistryResponseType response;
	private String user;

	private String event;
	
	public AuditContext() {
	}
	
	public void setResponse(RegistryResponseType response) {
		this.response = response;
	}
	
	public RegistryResponseType getResponse() {
		return this.response;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getUser() {
		return this.user;
	}
	
	public void setEvent(String event) {
		this.event = event;
	}
	
	public String getEvent() {
		return this.event;
	}
	
	public AuditableEventType getAuditableEvent() {
		
		AuditableEventType auditableEvent = ebRIMFactory.createAuditableEventType();
		
		// - ACTION (1..*)
		
		// Represents an action taken by the server within the context 
		// of an AuditableEventType instance. An AuditableEventType instance 
		// MUST have one or more Action instances.
		
		// - REQUEST-ID (1..1)
		
		// Specifies the id of the request that generated the AuditableEventType 
		// instance
		
		// - TIMESTAMP (1..1)
		
		// Specifies the timestamp that represents the date and time the event 
		// occurred
		
		// - USER (1..1)
		
		// Specifies the id of the registered user associated with the client 
		// that made the request to the server that generated the AuditableEventType 
		// instance. 
		
		// Note that the inherited attribute owner SHOULD be set by a server to an 
		// internal system user since it is the server and not the user associated 
		// with the request
		
		return null;

	}
}
