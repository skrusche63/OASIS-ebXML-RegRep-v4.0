package de.kp.registry.server.neo4j.spi;

import java.util.ArrayList;
import java.util.List;

import org.oasis.ebxml.registry.bindings.lcm.RemoveObjectsRequest;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefListType;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;
import org.oasis.ebxml.registry.bindings.rim.QueryType;

import de.kp.registry.server.neo4j.read.ReadManager;

public class RemoveRequestContext extends RequestContext {
	
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

		// specifies a query to be invoked; a server MUST remove all objects 
		// that match the specified query in addition to any other objects 
		// identified by the ObjectRefList element
		QueryType query = request.getQuery();
		
		// specifies a collection of references to existing RegistryObject 
		// instances in the server; a server MUST remove all objects that are 
		// referenced by this element in addition to any other objects identified 
		// by the Query element
		
		List<ObjectRefType> objectRefs = null;
		
		ObjectRefListType list = request.getObjectRefList();
		if (list != null) objectRefs = list.getObjectRef();

		// __DESIGN__
		
		// in order to remove registry objects, we first have to merge the objects
		// stemming from the provided query, and those described by the reference list
		if (query != null) {
		
			List<ObjectRefType> queryObjectRefs = ReadManager.getInstance().getObjectRefsByQuery(query);
			
			// merge with provided objectRefs
			if (objectRefs == null) objectRefs = new ArrayList<ObjectRefType>();
			objectRefs.addAll(queryObjectRefs);
			
		}

		this.list = objectRefs;

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
