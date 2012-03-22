package de.kp.registry.server.neo4j.auditing;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.oasis.ebxml.registry.bindings.rim.ActionType;
import org.oasis.ebxml.registry.bindings.rim.AuditableEventType;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefListType;

import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.server.neo4j.service.context.RequestContext;
import de.kp.registry.server.neo4j.service.context.ResponseContext;
import de.kp.registry.server.neo4j.util.CalendarUtil;

public class AuditContext {

	// reference to OASIS ebRIM object factory
	public static org.oasis.ebxml.registry.bindings.rim.ObjectFactory ebRIMFactory = new org.oasis.ebxml.registry.bindings.rim.ObjectFactory();

	private String comment;
	
	private String requestId;
	private String user;
	
	private RequestContext request;
	private ResponseContext response;
	
	public AuditContext(RequestContext request, ResponseContext response) {
		
		this.request = request;
		this.response = response;

		this.user = this.request.getUser();
		this.comment = this.request.getComment();

		this.requestId = this.response.getRequestId();
		
	}
	
	public String getComment() {
		return this.comment;
	}
	
	public String getUser() {
		return this.user;
	}

	public boolean isCreated() {		
		return (this.response.getCreated() != null);
	}

	public boolean isUpdated() {		
		return (this.response.getUpdated() != null);
	}

	public boolean isDeleted() {		
		return (this.response.getCreated() != null);
	}

	public AuditableEventType getAuditableEvent(String event) {
		
		AuditableEventType auditableEvent = ebRIMFactory.createAuditableEventType();
		
		// - ACTION (1..*)
		
		// Represents an action taken by the server within the context 
		// of an AuditableEventType instance. An AuditableEventType instance 
		// MUST have one or more Action instances.
		
		auditableEvent.getAction().add(createAction(event));
		
		// - REQUEST-ID (1..1)
		
		// Specifies the id of the request that generated the AuditableEventType 
		// instance
		
		auditableEvent.setRequestId(this.requestId);
		
		// - TIMESTAMP (1..1)
		
		// Specifies the timestamp that represents the date and time the event 
		// occurred
		
		auditableEvent.setTimestamp(createTimestamp());
		
		// - USER (1..1)
		
		// Specifies the id of the registered user associated with the client 
		// that made the request to the server that generated the AuditableEventType 
		// instance. 

		auditableEvent.setUser(this.user);
		
		// finally RegistryObjectTye specific parameters have to
		// be added to the AuditableEventType
		
		auditableEvent = fillBinding(auditableEvent);
		return auditableEvent;

	}
	
	private ActionType createAction(String event) {
		
		ActionType action = ebRIMFactory.createActionType();

		// - EVENT-TYPE (1..1)

		action.setEventType(event);
		
		// - AFFECTED-OBJECT (0..1)

		// __DESIGN__ 
		
		// a server generated ActionType refers to ObjectRefTypes only;
		// RegistryObjectTypes are NOT supported

		// - AFFECTED-OBJECT-REF (0..1)
		ObjectRefListType affectedObjectRefs = ebRIMFactory.createObjectRefListType();
		if (event.equals(CanonicalConstants.CREATED))
			affectedObjectRefs.getObjectRef().addAll(this.response.getCreated());
		
		else if (event.equals(CanonicalConstants.UPDATED))
			affectedObjectRefs.getObjectRef().addAll(this.response.getUpdated());
			
		else if (event.equals(CanonicalConstants.DELETED))
			affectedObjectRefs.getObjectRef().addAll(this.response.getDeleted());
		
		action.setAffectedObjectRefs(affectedObjectRefs);
		return action;
		
	}
	
	private XMLGregorianCalendar createTimestamp() {
		
		Date now = new Date();
		return CalendarUtil.toXMLGregorianCalendar(now);
		
	}
	
	private AuditableEventType fillBinding(AuditableEventType auditableEvent) {
		
		// Note that the inherited attribute owner SHOULD be set by a server to an 
		// internal system user since it is the server and not the user associated 
		// with the request

		// TODO

		return null;
	}
}
