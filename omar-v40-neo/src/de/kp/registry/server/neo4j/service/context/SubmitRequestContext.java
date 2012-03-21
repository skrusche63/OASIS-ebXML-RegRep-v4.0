package de.kp.registry.server.neo4j.service.context;

import java.util.List;

import org.oasis.ebxml.registry.bindings.lcm.Mode;
import org.oasis.ebxml.registry.bindings.lcm.SubmitObjectsRequest;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;


public class SubmitRequestContext extends RequestContext {

	private Mode mode;
	
	private Boolean checkReference;
	private List<RegistryObjectType>list;
	
	SubmitObjectsRequest request;
	
	public SubmitRequestContext(SubmitObjectsRequest request) {
		
		this.request = request;

		// Attribute comment – The comment attribute if specified contains a String that 
		// describes the request. A server MAY save this comment within a CommentType 
		// instance and associate it with the AuditableEvent(s) for that request.		
		this.comment = request.getComment();

		this.mode = request.getMode();
		this.checkReference = request.isCheckReferences();

		this.list = request.getRegistryObjectList().getRegistryObject();

	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}
	
	public String getMode() {
		return this.mode.value();
	}

	public void setCheckReference(Boolean checkReference) {
		this.checkReference = checkReference;
	}
	
	public Boolean isCheckReference() {
		return this.checkReference;
	}

	public void setList(List<RegistryObjectType> list) {
		this.list = list;
	}
	
	public List<RegistryObjectType> getList() {
		return list;
	}

}
