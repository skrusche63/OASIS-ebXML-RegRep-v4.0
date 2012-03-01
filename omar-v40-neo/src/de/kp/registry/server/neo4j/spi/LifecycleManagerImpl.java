package de.kp.registry.server.neo4j.spi;

import java.util.ArrayList;
import java.util.List;

import org.oasis.ebxml.registry.bindings.lcm.Mode;
import org.oasis.ebxml.registry.bindings.lcm.RemoveObjectsRequest;
import org.oasis.ebxml.registry.bindings.lcm.SubmitObjectsRequest;
import org.oasis.ebxml.registry.bindings.lcm.UpdateActionType;
import org.oasis.ebxml.registry.bindings.lcm.UpdateObjectsRequest;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefListType;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;
import org.oasis.ebxml.registry.bindings.rim.QueryType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectListType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;
import org.oasis.ebxml.registry.bindings.rs.RegistryResponseType;

import de.kp.registry.server.neo4j.database.ReadManager;
import de.kp.registry.server.neo4j.database.WriteManager;

public class LifecycleManagerImpl {

	public LifecycleManagerImpl() {		
	}

	public RegistryResponseType removeObjects(RemoveObjectsRequest request) {

		// retrieve request description parameters
		String deletionScope = request.getDeletionScope();
		Boolean checkReference = request.isCheckReferences();

		Boolean deleteChildren = request.isDeleteChildren();
				
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

		WriteManager wm = WriteManager.getInstance();
		return wm.removeObjects(objectRefs, checkReference, deleteChildren, deletionScope);
		
	}

	/*
	 * The SubmitObjects protocol allows a client to submit RegistryObjects to the server. 
	 * It also allows a client to completely replace existing RegistryObjects in the server.
	 */
	
	public RegistryResponseType submitObjects(SubmitObjectsRequest request) {
				
		// retrieve request description parameters
		Mode mode = request.getMode();
		Boolean checkReference = request.isCheckReferences();

		// retrieve provided registry object list
		RegistryObjectListType list = request.getRegistryObjectList();
		List<RegistryObjectType> objectTypes = list.getRegistryObject();

		// process request
		WriteManager wm = WriteManager.getInstance();
		return wm.submitObjects(objectTypes, checkReference, mode);
		
	}

	public RegistryResponseType updateObjects(UpdateObjectsRequest request) {

		// retrieve parameters from request
		Mode mode = request.getMode();
		Boolean checkReference = request.isCheckReferences();
		
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
		
		// specifies the details of how to update the target objects
		List<UpdateActionType> updateActions = request.getUpdateAction();
		
		// __DESIGN__
		
		// in order to remove registry objects, we first have to merge the objects
		// stemming from the provided query, and those described by the reference list
		if (query != null) {
		
			List<ObjectRefType> queryObjectRefs = ReadManager.getInstance().getObjectRefsByQuery(query);
			
			// merge with provided objectRefs
			if (objectRefs == null) objectRefs = new ArrayList<ObjectRefType>();
			objectRefs.addAll(queryObjectRefs);
			
		}
		
		WriteManager wm = WriteManager.getInstance();
		return wm.updateObjects(objectRefs, checkReference, mode, updateActions);

	}
}
