package de.kp.registry.server.neo4j.spi;

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
	
	public ResponseContext(String requestId) {

		this.response = ebRSFactory.createRegistryResponseType();
		
		// - REQUEST-ID
		
		// The id attribute must be specified by the client to uniquely 
		// identify a request. Its value SHOULD be a UUID URN.	

		this.response.setRequestId(requestId);		

	}
	
	public RegistryResponseType getResponse() {
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
	
	public void addNode(Node node) {

		String id = (String)node.getProperty(NEOBase.OASIS_RIM_ID);

		// in case of a successful creation of a registry object, the respective
		// object reference is added to the registry response
			
		ObjectRefType objectRef = ebRIMFactory.createObjectRefType();
		objectRef.setId(id);
		
		if (this.response.getObjectRefList() == null) this.response.setObjectRefList(ebRIMFactory.createObjectRefListType());
		this.response.getObjectRefList().getObjectRef().add(objectRef);

	}
}
