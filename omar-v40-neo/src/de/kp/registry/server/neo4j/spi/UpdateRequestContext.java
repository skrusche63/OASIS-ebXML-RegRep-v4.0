package de.kp.registry.server.neo4j.spi;

import java.util.List;

import org.oasis.ebxml.registry.bindings.lcm.Mode;
import org.oasis.ebxml.registry.bindings.lcm.UpdateActionType;
import org.oasis.ebxml.registry.bindings.lcm.UpdateObjectsRequest;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;

public class UpdateRequestContext {

	private String comment;
	private Mode mode;

	private Boolean checkReference;
	private List<UpdateActionType> updateActions;

	private List<ObjectRefType> list;
	UpdateObjectsRequest request;
	
	public UpdateRequestContext(UpdateObjectsRequest request) {
		
		this.request = request;
		
		// Attribute comment – The comment attribute if specified contains a String that 
		// describes the request. A server MAY save this comment within a CommentType 
		// instance and associate it with the AuditableEvent(s) for that request.		
		this.comment = request.getComment();

		// retrieve parameters from request
		this.mode = request.getMode();
		this.checkReference = request.isCheckReferences();
	
		// specifies the details of how to update the target objects
		this.updateActions = request.getUpdateAction();

	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return this.comment;
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

	public void setUpdateAction(List<UpdateActionType> updateActions) {
		this.updateActions = updateActions;
	}
	
	public List<UpdateActionType> getUpdateActions() {
		return this.updateActions;
	}
	
	public void setList(List<ObjectRefType> list) {
		this.list = list;
	}
	
	public List<ObjectRefType> getList() {
		return list;
	}

}
