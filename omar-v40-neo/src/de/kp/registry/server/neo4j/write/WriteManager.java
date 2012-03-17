package de.kp.registry.server.neo4j.write;

import java.lang.reflect.Method;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.lcm.Mode;
import org.oasis.ebxml.registry.bindings.lcm.UpdateActionType;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;
import org.oasis.ebxml.registry.bindings.rs.RegistryResponseType;

import de.kp.registry.server.neo4j.database.Database;
import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.exception.ExceptionManager;
import de.kp.registry.server.neo4j.domain.exception.InvalidRequestException;
import de.kp.registry.server.neo4j.domain.exception.ObjectExistsException;
import de.kp.registry.server.neo4j.domain.exception.ObjectNotFoundException;
import de.kp.registry.server.neo4j.notification.NotificationProcessor;
import de.kp.registry.server.neo4j.read.ReadManager;
import de.kp.registry.server.neo4j.spi.CanonicalConstants;
import de.kp.registry.server.neo4j.spi.RemoveRequestContext;
import de.kp.registry.server.neo4j.spi.SubmitRequestContext;
import de.kp.registry.server.neo4j.spi.UpdateRequestContext;

public class WriteManager {

	private static WriteManager instance = new WriteManager();

	// reference to OASIS ebRIM object factory
	public static org.oasis.ebxml.registry.bindings.rim.ObjectFactory ebRIMFactory = new org.oasis.ebxml.registry.bindings.rim.ObjectFactory();

	// reference to OASIS ebRS object factory
	public static org.oasis.ebxml.registry.bindings.rs.ObjectFactory ebRSFactory = new org.oasis.ebxml.registry.bindings.rs.ObjectFactory();

	private WriteManager() {		
	}
	
	public static WriteManager getInstance() {
		if (instance == null) instance = new WriteManager();
		return instance;
	}

	/************************************************************************
	 * 
	 * OASIS INTERFACE     OASIS INTERFACE     OASIS INTERFACE     OASIS
	 * 
	 ***********************************************************************/

	// this public method is used by the LifecycleManager
	public RegistryResponseType submitObjects(SubmitRequestContext context, RegistryResponseType response) {
		
		String modeValue = context.getMode();
		if (modeValue.equals(Mode.CREATE_ONLY)) {

			/*
			 * If an object does not exist, server MUST create it as a new object. If an object
			 * already exists, the server MUST return an ObjectExistsException fault message
			 */
			return createOnly(context, response);			
					
		} else if (modeValue.equals(Mode.CREATE_OR_REPLACE)) {
			
			/*
			 * If an object does not exist, server MUST create it as a new object.
			 * If an object already exists, server MUST replace the existing object 
			 * with the submitted object
			 */
			return createOrReplace(context, response);				
			
		} else if (modeValue.equals(Mode.CREATE_OR_VERSION)) {
			
			/*
			 * If an object does not exist, server MUST create it as a new object. If an object
			 * already exists, server MUST not alter the existing object and instead it MUST create
			 * a new version of the existing object using the state of the submitted object
			 */
			return createOrVersion(context, response);				
			
		}

		return null;
		
	}
	
	// this public method is used by the LifecycleManager
	public RegistryResponseType removeObjects(RemoveRequestContext context, RegistryResponseType response) {	

		ReadManager rm = ReadManager.getInstance();
		
		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {

			boolean result = false;

			Boolean checkReference = context.isCheckReference();
			Boolean deleteChildren = context.isDeleteChildren();
			
			String comment = context.getComment();
			String deletionScope = context.getDeletionScope();
			
			List<ObjectRefType> objectRefs = context.getList();
			for (ObjectRefType objectRef:objectRefs) {

				Node node = null;
				
				// determine whether the respective registry object already exists
				String nid = objectRef.getId();
				
				// __DESIGN__
				
				// it is expected, that the registry object is provided with a unique identifier
				node = rm.findNodeByID(nid);
				if (node != null) {

					result = delete(node, checkReference, deleteChildren, deletionScope, comment, response);
					
					// in case of a creation failure, the respective request
					// is terminated and the fill error message sent back
					if (result == false) break;

				}

			}
			
			if (result == true) {

				// unless success() is invoked, the transaction will fail upon finish()
				tx.success();

				// this is a successful request, we therefore indicate this status in the registry's response
				response.setStatus(CanonicalConstants.SUCCESS);
			
			}

		} finally {
			tx.finish();
		}

		// asynchronous invocation of notification service
		NotificationProcessor.getInstance().notify(response);		
		return response;

	}
	
	public RegistryResponseType updateObjects(UpdateRequestContext context, RegistryResponseType response) {

		String modeValue = context.getMode();
		if (modeValue.equals(Mode.CREATE_ONLY)) {

			/*
			 * This mode does not apply to UpdateObjectsRequest. If specified, server MUST
			 * return an InvalidRequestException
			 */
			
			ExceptionManager em = ExceptionManager.getInstance();		
			response.setStatus(CanonicalConstants.FAILURE);

			InvalidRequestException exception = new InvalidRequestException("[UpdatetObjectsRequest] The mode '" + modeValue + "' is not allowed.");
			response.getException().add(em.toBinding(exception));

			return response;
			
		} else if (modeValue.equals(Mode.CREATE_OR_REPLACE)) {
			
			/*
			 * If an object does not exist, server MUST return ObjectNotFoundException. If an object 
			 * already exists, server MUST update the existing object without creating a new version.	
			 */
			return updateOnly(context, response);	
			
		} else if (modeValue.equals(Mode.CREATE_OR_VERSION)) {
			
			/*
			 * If an object does not exist, server MUST return ObjectNotFoundException. If an object 
			 * already exists, server MUST create a new version of the existing object before applying 
			 * the requested update action.
			 */
			return updateAndVersion(context, response);	
			
		}

		return null;
	}
	
	// private methods to support the submitObjects request

	private RegistryResponseType createOnly(SubmitRequestContext context, RegistryResponseType response) {

		ReadManager rm = ReadManager.getInstance();

		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {

			boolean result = false;
			
			String comment = context.getComment();
			Boolean checkReference = context.isCheckReference();
			
			List<RegistryObjectType> registryObjects = context.getList();			
			for (RegistryObjectType registryObject:registryObjects) {
				
				Node node = null;
				
				// determine whether the respective registry object already exists
				String nid = registryObject.getId();
				
				// __DESIGN__
				
				// it is expected, that the registry object is provided with a unique identifier
				node = rm.findNodeByID(nid);
				if (node != null) {
					
					ExceptionManager em = ExceptionManager.getInstance();
					
					// If an object already exists, the server MUST return an 
					// ObjectExistsException fault message
				
					response.setStatus(CanonicalConstants.FAILURE);

					ObjectExistsException exception = new ObjectExistsException("[SubmitObjectsRequest] RegistryObjectType node with id '" + nid + "' already exist.");
					response.getException().add(em.toBinding(exception));
					
				} else {
					
					result = create(graphDB, registryObject, checkReference, comment, response);
					
					// in case of a creation failure, the respective request
					// is terminated and the fill error message sent back
					if (result == false) break;

				}
				
			}
			
			if (result == true) {

				// unless success() is invoked, the transaction will fail upon finish()
				tx.success();

				// this is a successful request, we therefore indicate this status in the registry's response
				response.setStatus(CanonicalConstants.SUCCESS);
			
			}
			
		} finally {
			tx.finish();
		}

		// asynchronous invocation of notification service
		NotificationProcessor.getInstance().notify(response);		
		return response;
		
	}

	private RegistryResponseType createOrReplace(SubmitRequestContext context, RegistryResponseType response) {

		ReadManager rm = ReadManager.getInstance();
		
		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {

			boolean result = false;
			
			String comment = context.getComment();
			Boolean checkReference = context.isCheckReference();

			List<RegistryObjectType> registryObjects = context.getList();			
			for (RegistryObjectType registryObject:registryObjects) {
				
				Node node = null;
				
				// determine whether the respective registry object already exists
				String nid = registryObject.getId();
				
				// __DESIGN__
				
				// it is expected, that the registry object is provided with a unique identifier
				node = rm.findNodeByID(nid);
				if (node != null) {					
					// if an object already exists, the server MUST replace the existing object with the submitted object
					result = replace(graphDB, node, registryObject, checkReference, comment, response);
					
				} else {
					// create a node within database for at least a registry object
					result = create(graphDB, registryObject, checkReference, comment, response);

				}
				
				// in case of a failure, the respective request
				// is terminated and the fill error message sent back
				if (result == false) break;
				
			}
			
			if (result == true) {

				// unless success() is invoked, the transaction will fail upon finish()
				tx.success();

				// this is a successful request, we therefore indicate this status in the registry's response
				response.setStatus(CanonicalConstants.SUCCESS);
			
			}
			
		} finally {
			tx.finish();
		}

		// asynchronous invocation of notification service
		NotificationProcessor.getInstance().notify(response);		
		return response;
		
	}

	private RegistryResponseType createOrVersion(SubmitRequestContext context, RegistryResponseType response) {

		ReadManager rm = ReadManager.getInstance();

		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {

			boolean result = false;
			
			String comment = context.getComment();
			Boolean checkReference = context.isCheckReference();
			
			List<RegistryObjectType> registryObjects = context.getList();			
			for (RegistryObjectType registryObject:registryObjects) {
				
				Node node = null;
				
				// determine whether the respective registry object already exists
				String nid = registryObject.getId();
				
				// __DESIGN__
				
				// it is expected, that the registry object is provided with a unique identifier
				node = rm.findNodeByID(nid);
				if (node != null) {					

					// If an object already exists, server MUST not alter the existing 
					// object and instead it MUST create a new version of the existing 
					// object using the state of the submitted object
					result = version(graphDB, node, registryObject, checkReference, comment, response);
					
				} else {					
					// create a node within database for at least a registry object
					result = create(graphDB, registryObject, checkReference, comment, response);
					
				}
				
				// in case of a failure, the respective request
				// is terminated and the fill error message sent back
				if (result == false) break;

			}
			
			if (result == true) {

				// unless success() is invoked, the transaction will fail upon finish()
				tx.success();

				// this is a successful request, we therefore indicate this status in the registry's response
				response.setStatus(CanonicalConstants.SUCCESS);
			
			}			
			
		} finally {
			tx.finish();
		}

		// asynchronous invocation of notification service
		NotificationProcessor.getInstance().notify(response);		
		return response;

	}

	// this method expects that no node with the unique identifier provided
	// with the registryObject exists in the database
	
	private boolean create(EmbeddedGraphDatabase graphDB, RegistryObjectType registryObject, Boolean checkReference, String comment, RegistryResponseType response) {
		
		// TODO: comment
		
		Node registryObjectTypeNode;

		try {
			// create new node within database
			registryObjectTypeNode = toNode(graphDB, registryObject, checkReference);

			String id = (String)registryObjectTypeNode.getProperty(NEOBase.OASIS_RIM_ID);
			addIdToResponse(id, response);

			return true;
			
		} catch (Exception e) {
						
			ExceptionManager em = ExceptionManager.getInstance();
			
			response.setStatus(CanonicalConstants.FAILURE);
			response.getException().add(em.toBinding(e));
		
		}
		
		return false;
		
	}

	private boolean replace(EmbeddedGraphDatabase graphDB, Node node, RegistryObjectType registryObject, Boolean checkReference, String comment, RegistryResponseType response) {

		// TODO: comment
		
		// in case of a successful replacement of this registry object, the 
		// respective unique identifier is assigned to the registry response

		Node registryObjectTypeNode;

		try {
			// replace existing node within database
			registryObjectTypeNode = fillNode(graphDB, node, registryObject, checkReference);

			String id = (String)registryObjectTypeNode.getProperty(NEOBase.OASIS_RIM_ID);
			addIdToResponse(id, response);

			return true;
			
		} catch (Exception e) {
			
			ExceptionManager em = ExceptionManager.getInstance();
			
			response.setStatus(CanonicalConstants.FAILURE);
			response.getException().add(em.toBinding(e));
			
		}
		
		return false;

	}

	private boolean update(Node node, Boolean checkReference, List<UpdateActionType> updateActions, String comment, RegistryResponseType response) {

		// TODO: comment
		
		try {

			String id = (String)node.getProperty(NEOBase.OASIS_RIM_ID);
			updateNode(node, checkReference, updateActions);
			
			// fill response with unique identifier of the registry
			// object that has been successfully removed
			
			addIdToResponse(id, response);
			return true;
			
		} catch (Exception e) {
						
			ExceptionManager em = ExceptionManager.getInstance();
			
			response.setStatus(CanonicalConstants.FAILURE);
			response.getException().add(em.toBinding(e));

		}

		return false;
		
	}
	
	private boolean version(EmbeddedGraphDatabase graphDB, Node node, RegistryObjectType registryObject, Boolean checkReference, String comment, RegistryResponseType response) {

		// TODO: comment
		
		try {

			String id = (String)node.getProperty(NEOBase.OASIS_RIM_ID);
			versionNode(graphDB, node, registryObject, checkReference);
			
			// fill response with unique identifier of the registry
			// object that has been successfully removed
			
			addIdToResponse(id, response);
			return true;
			
		} catch (Exception e) {
						
			ExceptionManager em = ExceptionManager.getInstance();
			
			response.setStatus(CanonicalConstants.FAILURE);
			response.getException().add(em.toBinding(e));

		}

		return false;
		
	}

	private boolean delete(Node node, Boolean checkReference, Boolean deleteChildren, String deletionScope, String comment, RegistryResponseType response) {
		
		// TODO: comment
		
		try {

			String id = (String)node.getProperty(NEOBase.OASIS_RIM_ID);
			removeNode(node, checkReference, deleteChildren, deletionScope);
			
			// fill response with unique identifier of the registry
			// object that has been successfully removed
			
			addIdToResponse(id, response);
			return true;
			
		} catch (Exception e) {
						
			ExceptionManager em = ExceptionManager.getInstance();
			
			response.setStatus(CanonicalConstants.FAILURE);
			response.getException().add(em.toBinding(e));

		}

		return false;
		
	}

	/*
	 * If an object does not exist, server MUST return ObjectNotFoundException. If an object 
	 * already exists, server MUST update the existing object without creating a new version.	
	 */

	private RegistryResponseType updateOnly(UpdateRequestContext context, RegistryResponseType response) {

		ReadManager rm = ReadManager.getInstance();

		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {

			boolean result = false;

			String comment = context.getComment();			
			Boolean checkReference = context.isCheckReference();
			
			List<ObjectRefType> objectRefs = context.getList();
			List<UpdateActionType> updateActions = context.getUpdateActions();
			
			for (ObjectRefType objectRef:objectRefs) {
				
				Node node = null;
				
				// determine whether the respective registry object already exists
				String nid = objectRef.getId();
				
				// __DESIGN__
				
				// it is expected, that the registry object is provided with a unique identifier
				node = rm.findNodeByID(nid);
				if (node == null) {
					
					ExceptionManager em = ExceptionManager.getInstance();
				
					response.setStatus(CanonicalConstants.FAILURE);

					ObjectNotFoundException exception = new ObjectNotFoundException("[UpdateObjectsRequest] ObjectRefType node with id '" + nid + "' does not exist.");
					response.getException().add(em.toBinding(exception));
					
				} else {

					result = update(node, checkReference, updateActions, comment, response);

					// in case of a failure, the respective request
					// is terminated and the error message sent back
					if (result == false) break;

				}
				
			}
			
			if (result == true) {

				// unless success() is invoked, the transaction will fail upon finish()
				tx.success();

				// this is a successful request, we therefore indicate this status in the registry's response
				response.setStatus(CanonicalConstants.SUCCESS);
			
			}
			
		} finally {
			tx.finish();
		}

		// asynchronous invocation of notification service
		NotificationProcessor.getInstance().notify(response);		
		return response;

	}

	/*
	 * If an object does not exist, server MUST return ObjectNotFoundException. If an object 
	 * already exists, server MUST create a new version of the existing object before applying 
	 * the requested update action.
	 */
	
	private RegistryResponseType updateAndVersion(UpdateRequestContext context, RegistryResponseType response) {

		ReadManager rm = ReadManager.getInstance();

		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {

			boolean result = false;

			String comment = context.getComment();			
			Boolean checkReference = context.isCheckReference();
			
			List<ObjectRefType> objectRefs = context.getList();
			List<UpdateActionType> updateActions = context.getUpdateActions();
			
			for (ObjectRefType objectRef:objectRefs) {
				
				Node node = null;
				
				// determine whether the respective registry object already exists
				String nid = objectRef.getId();
				
				// __DESIGN__
				
				// it is expected, that the registry object is provided with a unique identifier
				node = rm.findNodeByID(nid);
				if (node == null) {
					
					ExceptionManager em = ExceptionManager.getInstance();
				
					response.setStatus(CanonicalConstants.FAILURE);

					ObjectNotFoundException exception = new ObjectNotFoundException("[UpdateObjectsRequest] ObjectRefType node with id '" + nid + "' does not exist.");
					response.getException().add(em.toBinding(exception));
					
				} else {

					result = version(graphDB, node, null, checkReference, comment, response);
					if (result == true) result = update(node, checkReference, updateActions, comment, response);

					// in case of a failure, the respective request
					// is terminated and the error message sent back
					if (result == false) break;

				}
				
			}
			
			if (result == true) {

				// unless success() is invoked, the transaction will fail upon finish()
				tx.success();

				// this is a successful request, we therefore indicate this status in the registry's response
				response.setStatus(CanonicalConstants.SUCCESS);
			
			}
			
		} finally {
			tx.finish();
		}

		// asynchronous invocation of notification service
		NotificationProcessor.getInstance().notify(response);		
		return response;

	}
	
	// this method create a new node in the database for at least a registry object
	private Node toNode(EmbeddedGraphDatabase graphDB, Object binding, Boolean checkReference) throws Exception {

		Class<?> clazz = NEOBase.getClassNEO(binding);

	    Method method = clazz.getMethod("toNode", graphDB.getClass(), Object.class, Boolean.class);
	    return (Node) method.invoke(null, graphDB, binding, checkReference);
    	
	}
	
	private Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, Boolean checkReference) throws Exception {

		Class<?> clazz = NEOBase.getClassNEO(binding);

	    Method method = clazz.getMethod("fillNode", graphDB.getClass(), Node.class, Object.class, Boolean.class);
	    return (Node) method.invoke(null, graphDB, node, binding, checkReference);
    	
	}
	
	private void removeNode(Node node, Boolean checkReference, Boolean deleteChildren, String deletionScope) throws Exception {

		String bindingName = (String)node.getProperty(NEOBase.NEO4J_TYPE);
		Class<?> clazz = NEOBase.getClassNEOByName(bindingName);

	    Method method = clazz.getMethod("removeNode", Node.class, boolean.class, boolean.class, String.class);
	    method.invoke(null, node, checkReference, deleteChildren, deletionScope);

	}

	// __DESIGN_
	
	// as a first step a binding object is created from the node provided;
	//
	// as a second step, the binding object is updated according to the
	// update actions provided with this request
	//
	// as a third step, the node is filled from the binding object generated
	
	private void updateNode(Node node, Boolean checkReference, List<UpdateActionType> updateActions) throws Exception {	

		ReadManager rm = ReadManager.getInstance();

		String language = null;
		Object binding = rm.toBinding(node, language);
		
		// update of a certain node is delegated to the UpdateProcessor
		UpdateProcessor processor = new UpdateProcessor();
		processor.process(node, binding, checkReference, updateActions);
		
	}

	/*
	 * If an object already exists, server MUST not alter the existing object and instead 
	 * it MUST create a new version of the existing object using the state of the submitted object
	 */

	// __DESIGN_
	
	// as a first step a clone of an existing node is built that respects all properties
	// and relations of the original node, except:
	//
	// - the existing unique identifier is extended by using the new version name
	// - the (optionally) existing reference to a VersionInfoType node is replaced
	//   by a VersionInfoType node that carries the new version name
	//
	// as a second step, the fillNode mechanism is used except for the version information
	
	private void versionNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws Exception {

		Node target = NEOBase.cloneAndVersionNode(graphDB, node);
		
		// in case of an update request, the provided binding object may be null
		if (binding == null) return;

		// in case of a versioning request, the VersionInfoType is excluded from
		// the fill request, as the respective information is already set with
		// cloneAndVersionNode
		
		boolean excludeVersion = true;
		
		String bindingName = (String)target.getProperty(NEOBase.NEO4J_TYPE);
		Class<?> clazz = NEOBase.getClassNEOByName(bindingName);

	    Method method = clazz.getMethod("fillNode", graphDB.getClass(), Node.class, Object.class, boolean.class, boolean.class);
	    method.invoke(null, graphDB, node, binding, checkReference, excludeVersion);

	    // TODO: indexing
	    
	}

	// in case of a successful creation of a registry object, the respective
	// object reference is added to the registry response

	private void addIdToResponse(String id, RegistryResponseType response) {
		
		ObjectRefType objectRef = ebRIMFactory.createObjectRefType();
		objectRef.setId(id);
		
		if (response.getObjectRefList() == null) response.setObjectRefList(ebRIMFactory.createObjectRefListType());
		response.getObjectRefList().getObjectRef().add(objectRef);

	}

	/************************************************************************
	 * 
	 * TEST INTERFACE     TEST INTERFACE     TEST INTERFACE     TEST
	 * 
	 ***********************************************************************/
	
	public void write(List<Object> bindings) throws Exception {
		
		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {

			for (Object binding:bindings) {
				writeInternal(graphDB, binding);				
			}
			
			tx.success();
			
		} finally {
			tx.finish();
		}

	}
	
	public void write(Object binding) throws Exception {
		
		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {
			
			writeInternal(graphDB, binding);
			tx.success();
			
		} finally {
			tx.finish();
		}

	}
	
	private void writeInternal(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {		
		toNode(graphDB, binding, false);			
	}

}
