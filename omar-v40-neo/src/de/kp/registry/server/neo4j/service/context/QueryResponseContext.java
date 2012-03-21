package de.kp.registry.server.neo4j.service.context;

import java.math.BigInteger;

import org.oasis.ebxml.registry.bindings.query.QueryResponse;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;

import de.kp.registry.server.neo4j.domain.exception.ExceptionManager;

public class QueryResponseContext {

	// reference to OASIS ebQuery object factory
	public static org.oasis.ebxml.registry.bindings.query.ObjectFactory ebQueryFactory = new org.oasis.ebxml.registry.bindings.query.ObjectFactory();

	// reference to OASIS ebRIM object factory
	public static org.oasis.ebxml.registry.bindings.rim.ObjectFactory ebRIMFactory = new org.oasis.ebxml.registry.bindings.rim.ObjectFactory();

	private QueryResponse response;
	
	public QueryResponseContext(String requestId) {
		
		this.response = ebQueryFactory.createQueryResponse();
		
		// - REQUEST-ID
		
		// The id attribute must be specified by the client to uniquely 
		// identify a request. Its value SHOULD be a UUID URN.	

		this.response.setRequestId(requestId);		

		if (this.response.getRegistryObjectList() == null) this.response.setRegistryObjectList(ebRIMFactory.createRegistryObjectListType());
		if (this.response.getObjectRefList() == null) this.response.setObjectRefList(ebRIMFactory.createObjectRefListType());

	}
	
	public QueryResponse getQueryResponse() {
		return this.response;
	}
	
	public String getRequestId() {
		return this.response.getRequestId();
	}
	
	public void setStatus(String status) {
		this.response.setStatus(status);
	}
	
	public void setStartIndex(BigInteger startIndex) {
		this.response.setStartIndex(startIndex);
	}

	public void setTotalResultCount(BigInteger totalResultCount) {
		this.response.setTotalResultCount(totalResultCount);
	}

	public void addRegistryObject(RegistryObjectType registryObject) {
		this.response.getRegistryObjectList().getRegistryObject().add(registryObject);
	}

	public void addObjectRef(ObjectRefType objectRef) {
		this.response.getObjectRefList().getObjectRef().add(objectRef);
	}

	public void addException(Exception exception) {
		
		ExceptionManager em = ExceptionManager.getInstance();
		this.response.getException().add(em.toBinding(exception));

	}

	// this method clears all registry objects from the response
	public void clearRegistryObject() {
		this.response.getRegistryObjectList().getRegistryObject().clear();
	}

	// this method clears all object refs from the response
	public void clearObjectRef() {
		this.response.getObjectRefList().getObjectRef().clear();
	}
}
