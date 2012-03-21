package de.kp.registry.server.neo4j.common;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;
import org.oasis.ebxml.registry.bindings.rs.RegistryResponseType;

import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.exception.ExceptionManager;

public class ResponseContext {

	// reference to OASIS ebRIM object factory
	public static org.oasis.ebxml.registry.bindings.rim.ObjectFactory ebRIMFactory = new org.oasis.ebxml.registry.bindings.rim.ObjectFactory();

	// reference to OASIS ebRS object factory
	public static org.oasis.ebxml.registry.bindings.rs.ObjectFactory ebRSFactory = new org.oasis.ebxml.registry.bindings.rs.ObjectFactory();

	private RegistryResponseType response;

	private List<ObjectRefType> created;
	private List<ObjectRefType> updated;
	private List<ObjectRefType> deleted;
	
	public ResponseContext() {	
	}
	
	public ResponseContext(String requestId) {

		// initialize audit support data structures
		created = new ArrayList<ObjectRefType>();
		
		updated = new ArrayList<ObjectRefType>();
		deleted = new ArrayList<ObjectRefType>();
		
		this.response = ebRSFactory.createRegistryResponseType();
		
		// - REQUEST-ID
		
		// The id attribute must be specified by the client to uniquely 
		// identify a request. Its value SHOULD be a UUID URN.	

		this.response.setRequestId(requestId);		

		// - OBJECTREF-LIST
		
		if (this.response.getObjectRefList() == null) this.response.setObjectRefList(ebRIMFactory.createObjectRefListType());

	}
	
	public void setRegistryResponse(RegistryResponseType response) {
		this.response = response;
	}
	
	public RegistryResponseType getRegistryResponse() {
		return this.response;
	}
	
	public String getRequestId() {
		return this.response.getRequestId();
	}
	
	public void setStatus(String status) {
		this.response.setStatus(status);
	}
	
	public void addException(Exception exception) {
		
		ExceptionManager em = ExceptionManager.getInstance();
		this.response.getException().add(em.toBinding(exception));

	}
	
	public void addCreated(Node node) {

		ObjectRefType objectRef = createObjectRef(node);
		created.add(objectRef);
		
		this.response.getObjectRefList().getObjectRef().add(objectRef);
		
	}

	public List<ObjectRefType> getCreated() {		
		return (created.size() == 0) ? null : created;
	}
	
	public void addUpdated(Node node) {

		ObjectRefType objectRef = createObjectRef(node);
		updated.add(objectRef);
		
		this.response.getObjectRefList().getObjectRef().add(objectRef);
		
	}

	public List<ObjectRefType> getUpdated() {		
		return (updated.size() == 0) ? null : updated;
	}

	public void addDeleted(Node node) {

		ObjectRefType objectRef = createObjectRef(node);
		deleted.add(objectRef);
		
		this.response.getObjectRefList().getObjectRef().add(objectRef);
		
	}

	public List<ObjectRefType> getDeleted() {		
		return (deleted.size() == 0) ? null : deleted;
	}

	private ObjectRefType createObjectRef(Node node) {

		String id = (String)node.getProperty(NEOBase.OASIS_RIM_ID);
			
		ObjectRefType objectRef = ebRIMFactory.createObjectRefType();
		objectRef.setId(id);
		
		return objectRef;
		
	}
}
