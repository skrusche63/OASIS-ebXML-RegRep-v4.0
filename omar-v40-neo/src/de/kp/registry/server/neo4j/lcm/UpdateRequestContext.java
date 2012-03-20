package de.kp.registry.server.neo4j.lcm;

import java.util.ArrayList;
import java.util.List;

import org.oasis.ebxml.registry.bindings.lcm.Mode;
import org.oasis.ebxml.registry.bindings.lcm.UpdateActionType;
import org.oasis.ebxml.registry.bindings.lcm.UpdateObjectsRequest;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefListType;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;
import org.oasis.ebxml.registry.bindings.rim.QueryType;

import de.kp.registry.server.neo4j.common.RequestContext;
import de.kp.registry.server.neo4j.read.ReadManager;

public class UpdateRequestContext extends RequestContext {

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

		// specifies a query to be invoked; a server MUST use all objects that match 
		// the specified query in addition to any other objects identified by the
		// ObjectRefList element as targets of the update action
		QueryType query = request.getQuery();
		
		// specifies a collection of references to existing RegistryObject instances
		// in the server; a server MUST use all objects that are referenced by this 
		// element in addition to any other objects identified by the Query element 
		// as targets of the update action
		
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
