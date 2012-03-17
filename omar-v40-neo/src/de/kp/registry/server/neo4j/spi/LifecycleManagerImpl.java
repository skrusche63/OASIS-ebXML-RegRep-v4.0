package de.kp.registry.server.neo4j.spi;

import java.util.ArrayList;
import java.util.List;

import org.oasis.ebxml.registry.bindings.lcm.RemoveObjectsRequest;
import org.oasis.ebxml.registry.bindings.lcm.SubmitObjectsRequest;
import org.oasis.ebxml.registry.bindings.lcm.UpdateObjectsRequest;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefListType;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;
import org.oasis.ebxml.registry.bindings.rim.QueryType;
import org.oasis.ebxml.registry.bindings.rs.RegistryResponseType;

import de.kp.registry.server.neo4j.read.ReadManager;
import de.kp.registry.server.neo4j.write.WriteManager;

public class LifecycleManagerImpl {

	// reference to OASIS ebRS object factory
	public static org.oasis.ebxml.registry.bindings.rs.ObjectFactory ebRSFactory = new org.oasis.ebxml.registry.bindings.rs.ObjectFactory();

	public LifecycleManagerImpl() {		
	}

	public RegistryResponseType removeObjects(RemoveObjectsRequest request) {
		
		RemoveRequestContext removeContext = new RemoveRequestContext(request);
		
		// Attribute id – The id attribute must be specified by the client to uniquely 
		// identify a request. Its value SHOULD be a UUID URN.	
		String requestId = request.getId();
		RegistryResponseType removeResponse = createResponse(requestId);
				
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

		removeContext.setList(objectRefs);
		
		WriteManager wm = WriteManager.getInstance();
		return wm.removeObjects(removeContext, removeResponse);
		
	}

	/*
	 * The SubmitObjects protocol allows a client to submit RegistryObjects to the server. 
	 * It also allows a client to completely replace existing RegistryObjects in the server.
	 */
	
	public RegistryResponseType submitObjects(SubmitObjectsRequest request) {
		
		SubmitRequestContext submitContext = new SubmitRequestContext(request);
		
		// Attribute id – The id attribute must be specified by the client to uniquely 
		// identify a request. Its value SHOULD be a UUID URN.	
		String requestId = request.getId();
		RegistryResponseType submitResponse = createResponse(requestId);

		// process request
		WriteManager wm = WriteManager.getInstance();
		return wm.submitObjects(submitContext, submitResponse);
		
	}

	public RegistryResponseType updateObjects(UpdateObjectsRequest request) {

		UpdateRequestContext updateContext = new UpdateRequestContext(request);
		
		// Attribute id – The id attribute must be specified by the client to uniquely 
		// identify a request. Its value SHOULD be a UUID URN.	
		String requestId = request.getId();
		RegistryResponseType updateResponse = createResponse(requestId);
		
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

		updateContext.setList(objectRefs);

		WriteManager wm = WriteManager.getInstance();
		return wm.updateObjects(updateContext, updateResponse);

	}
	
	private RegistryResponseType createResponse(String requestId) {
		
		RegistryResponseType registryResponseType = ebRSFactory.createRegistryResponseType();
		
		// - REQUEST-ID
		registryResponseType.setRequestId(requestId);		
		return registryResponseType;
		
	}


}
