package de.kp.registry.server.neo4j.spi;

import java.util.List;

import org.oasis.ebxml.registry.bindings.lcm.RemoveObjectsRequest;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;

public class RemoveRequestContext {
	
	private String comment;
	private Boolean checkReference;

	private String deletionScope;
	private Boolean deleteChildren;

	private List<ObjectRefType> list;
	
	RemoveObjectsRequest request;
	
	public RemoveRequestContext(RemoveObjectsRequest request) {
		
		this.request = request;
				
		// Attribute comment – The comment attribute if specified contains a String that 
		// describes the request. A server MAY save this comment within a CommentType 
		// instance and associate it with the AuditableEvent(s) for that request.		
		
		this.comment = request.getComment();

		this.deletionScope = request.getDeletionScope();
		
		this.checkReference = request.isCheckReferences();
		this.deleteChildren = request.isDeleteChildren();

	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return this.comment;
	}

	public void setCheckReference(Boolean checkReference) {
		this.checkReference = checkReference;
	}
	
	public Boolean isCheckReference() {
		return this.checkReference;
	}

	public void setDeleteChildren(Boolean deleteChildren) {
		this.deleteChildren = deleteChildren;
	}
	
	public Boolean isDeleteChildren() {
		return this.deleteChildren;
	}

	public void setDeletionScope(String deletionScope) {
		this.deletionScope = deletionScope;
	}
	
	public String getDeletionScope() {
		return this.deletionScope;
	}

	public void setList(List<ObjectRefType> list) {
		this.list = list;
	}
	
	public List<ObjectRefType> getList() {
		return list;
	}
}
