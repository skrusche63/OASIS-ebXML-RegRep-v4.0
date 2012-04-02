package de.kp.registry.server.neo4j.write;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.lcm.Mode;
import org.oasis.ebxml.registry.bindings.lcm.UpdateActionType;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;

import de.kp.registry.common.CanonicalSchemes;
import de.kp.registry.server.neo4j.database.Database;
import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.exception.CatalogingException;
import de.kp.registry.server.neo4j.domain.exception.InvalidRequestException;
import de.kp.registry.server.neo4j.domain.exception.ObjectExistsException;
import de.kp.registry.server.neo4j.domain.exception.ObjectNotFoundException;
import de.kp.registry.server.neo4j.read.ReadManager;
import de.kp.registry.server.neo4j.service.context.CatalogRequestContext;
import de.kp.registry.server.neo4j.service.context.CatalogResponseContext;
import de.kp.registry.server.neo4j.service.context.RemoveRequestContext;
import de.kp.registry.server.neo4j.service.context.ResponseContext;
import de.kp.registry.server.neo4j.service.context.SubmitRequestContext;
import de.kp.registry.server.neo4j.service.context.UpdateRequestContext;
import de.kp.registry.server.neo4j.write.plugin.CatalogerPlugin;

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

	/*
	 * The server's WreiteManager SHOULD delegate catalogObjects operation to any number 
	 * of Cataloger plugins using the following algorithm:
	 * 
	 * (1) The server selects the RegistryObjects that are the target of the catalogObjects 
	 *     operations using the <spi:Query> and <rim:ObjectRefList> elements. Any objects 
	 *     specified by the OriginalObjects element MUST be ignored by the server.
	 *     
	 * (2) The server partitions the set of target objects into multiple sets based upon the 
	 *     objectType attribute value for the target objects.
	 *     
	 * (3) The server determines whether there is a Cataloger plugin configured for each objectType 
	 *     for which there is a set of target objects
	 *     
	 * (4) For each set of target objects that share a common objectType and for which there is a 
	 *     configured Cataloger plugin, the server MUST invoke the Cataloger plugin. The Cataloger 
	 *     plugin invocation MUST specify the target objects for that set using the OriginalObjects 
	 *     element. The server MUST NOT specify <spi:Query> and <rim:ObjectRefList> elements when 
	 *     invoking catalogObjects operation on a Cataloger plugin
	 *     
	 * (5) Each Cataloger plugin MUST process the CatalogObjectsRquest and return a CatalogObjects-
	 *     Response or fault message to the server's Cataloger endpoint.
	 *     
	 * (6) The server's Cataloger endpoint MUST then combine the results of the individual CatalogObjects-
	 *      Request to Cataloger plugins and commit these objects as part of the transaction associated with
	 *      the request. It MUST then combine the individual CatalogObjectsResponse messages into a single 
	 *      unified CatalogObjectsResponse and return it to the client.
	 *
	 */
	
	public ResponseContext catalogObjects(CatalogRequestContext request, CatalogResponseContext response) {
		
		List<ObjectRefType> objectRefs = request.getObjectRefs();
		if ((objectRefs == null ) || (objectRefs.size() == 0)) {

			CatalogingException exception = new CatalogingException("[CatalogObjectsRequest] No objects to catalog.");
			response.addException(exception);
			
			return response;
			
		} else {
			
			boolean result = true;
			
			Map<String,List<Node>> partitionedNodes = partitionNodes(objectRefs);
			Set<String> objectTypes = partitionedNodes.keySet();
			
			Set<RegistryObjectType> catalogedObjects = new HashSet<RegistryObjectType>();
			
			// determine plugin from the key of the partitionedNodes
			CatalogerPluginFactory factory = CatalogerPluginFactory.getInstance();
			for (String objectType:objectTypes) {

				CatalogerPlugin cataloger = factory.getCatalogerPlugin(objectType);
				if (cataloger == null) continue;
				
				try {
					catalogedObjects.addAll(catalogObjects(partitionedNodes.get(objectType), cataloger));

				} catch (Exception e) {

					CatalogingException exception = new CatalogingException("[CatalogObjectsRequest] Cataloging objects failed for " + objectType + ".");
					response.addException(exception);
					
					result = false;
					break;

				}
				
			}

			if (result == true) {
			
				// the cataloged objects are registered with the OASIS ebXML RegRep v4.0 
				List<RegistryObjectType> registryObjects = new ArrayList<RegistryObjectType>(catalogedObjects);
				response = (CatalogResponseContext)createOrVersionInternal(registryObjects, false, response);
			
			}
		
		}

		return response;

	}

	// this public method is used by the LifecycleManager
	public ResponseContext submitObjects(SubmitRequestContext request, ResponseContext response) {
		
		String modeValue = request.getMode();
		if (modeValue.equals(Mode.CREATE_ONLY)) {

			/*
			 * If an object does not exist, server MUST create it as a new object. If an object
			 * already exists, the server MUST return an ObjectExistsException fault message
			 */
			return createOnly(request, response);			
					
		} else if (modeValue.equals(Mode.CREATE_OR_REPLACE)) {
			
			/*
			 * If an object does not exist, server MUST create it as a new object.
			 * If an object already exists, server MUST replace the existing object 
			 * with the submitted object
			 */
			return createOrReplace(request, response);				
			
		} else if (modeValue.equals(Mode.CREATE_OR_VERSION)) {
			
			/*
			 * If an object does not exist, server MUST create it as a new object. If an object
			 * already exists, server MUST not alter the existing object and instead it MUST create
			 * a new version of the existing object using the state of the submitted object
			 */
			return createOrVersion(request, response);				
			
		}

		return null;
		
	}
	
	// this public method is used by the LifecycleManager
	public ResponseContext removeObjects(RemoveRequestContext request, ResponseContext response) {	

		ReadManager rm = ReadManager.getInstance();
		
		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {

			boolean result = false;

			Boolean checkReference = request.isCheckReference();
			Boolean deleteChildren = request.isDeleteChildren();
			
			String deletionScope = request.getDeletionScope();
			
			List<ObjectRefType> objectRefs = request.getList();
			for (ObjectRefType objectRef:objectRefs) {

				Node node = null;
				
				// determine whether the respective registry object already exists
				String nid = objectRef.getId();
				
				// __DESIGN__
				
				// it is expected, that the registry object is provided with a unique identifier
				node = rm.findNodeByID(nid);
				if (node != null) {

					result = delete(node, checkReference, deleteChildren, deletionScope, response);
					
					// in case of a creation failure, the respective request
					// is terminated and the fill error message sent back
					if (result == false) break;

				}

			}
			
			if (result == true) {

				// unless success() is invoked, the transaction will fail upon finish()
				tx.success();

				// this is a successful request, we therefore indicate this status in the registry's response
				response.setStatus(CanonicalSchemes.CANONICAL_RESPONSE_STATUS_TYPE_ID_Success);
			
			}

		} finally {
			tx.finish();
		}
		
		// return response
		return response;

	}
	
	public ResponseContext updateObjects(UpdateRequestContext request, ResponseContext response) {

		String modeValue = request.getMode();
		if (modeValue.equals(Mode.CREATE_ONLY)) {

			/*
			 * This mode does not apply to UpdateObjectsRequest. If specified, server MUST
			 * return an InvalidRequestException
			 */

			InvalidRequestException exception = new InvalidRequestException("[UpdateObjectsRequest] The mode '" + modeValue + "' is not allowed.");
			response.addException(exception);
			
			return response;
			
		} else if (modeValue.equals(Mode.CREATE_OR_REPLACE)) {
			
			/*
			 * If an object does not exist, server MUST return ObjectNotFoundException. If an object 
			 * already exists, server MUST update the existing object without creating a new version.	
			 */
			return updateOnly(request, response);	
			
		} else if (modeValue.equals(Mode.CREATE_OR_VERSION)) {
			
			/*
			 * If an object does not exist, server MUST return ObjectNotFoundException. If an object 
			 * already exists, server MUST create a new version of the existing object before applying 
			 * the requested update action.
			 */
			return updateAndVersion(request, response);	
			
		}

		return null;
	}

	// private methods to support the submitObjects request

	private ResponseContext createOnly(SubmitRequestContext request, ResponseContext response) {
		
		ReadManager rm = ReadManager.getInstance();

		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {

			boolean result = false;
			
			Boolean checkReference = request.isCheckReference();
			
			List<RegistryObjectType> registryObjects = request.getList();			
			for (RegistryObjectType registryObject:registryObjects) {
				
				Node node = null;
				
				// determine whether the respective registry object already exists
				String nid = registryObject.getId();
				
				// __DESIGN__
				
				// it is expected, that the registry object is provided with a unique identifier
				node = rm.findNodeByID(nid);
				if (node != null) {
					
					// If an object already exists, the server MUST return an 
					// ObjectExistsException fault message

					ObjectExistsException exception = new ObjectExistsException("[SubmitObjectsRequest] RegistryObjectType node with id '" + nid + "' already exist.");
					response.addException(exception);
					
				} else {
					
					result = create(graphDB, registryObject, checkReference, response);
					
					// in case of a creation failure, the respective request
					// is terminated and the fill error message sent back
					if (result == false) break;

				}
				
			}
			
			if (result == true) {

				// unless success() is invoked, the transaction will fail upon finish()
				tx.success();

				// this is a successful request, we therefore indicate this status in the registry's response
				response.setStatus(CanonicalSchemes.CANONICAL_RESPONSE_STATUS_TYPE_ID_Success);
			
			}
			
		} finally {
			tx.finish();
		}
		
		// return response
		return response;
		
	}

	private ResponseContext createOrReplace(SubmitRequestContext request, ResponseContext response) {

		ReadManager rm = ReadManager.getInstance();
		
		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {

			boolean result = false;
			
			Boolean checkReference = request.isCheckReference();

			List<RegistryObjectType> registryObjects = request.getList();			
			for (RegistryObjectType registryObject:registryObjects) {
				
				Node node = null;
				
				// determine whether the respective registry object already exists
				String nid = registryObject.getId();
				
				// __DESIGN__
				
				// it is expected, that the registry object is provided with a unique identifier
				node = rm.findNodeByID(nid);
				if (node != null) {					
					// if an object already exists, the server MUST replace the existing object with the submitted object
					result = replace(graphDB, node, registryObject, checkReference, response);
					
				} else {
					// create a node within database for at least a registry object
					result = create(graphDB, registryObject, checkReference, response);

				}
				
				// in case of a failure, the respective request
				// is terminated and the fill error message sent back
				if (result == false) break;
				
			}
			
			if (result == true) {

				// unless success() is invoked, the transaction will fail upon finish()
				tx.success();

				// this is a successful request, we therefore indicate this status in the registry's response
				response.setStatus(CanonicalSchemes.CANONICAL_RESPONSE_STATUS_TYPE_ID_Success);
			
			}
			
		} finally {
			tx.finish();
		}
		
		// return response
		return response;
		
	}

	private ResponseContext createOrVersion(SubmitRequestContext request, ResponseContext response) {

		// extract request specific parameters
		boolean checkReference = request.isCheckReference();		
		List<RegistryObjectType> registryObjects = request.getList();			

		return createOrVersionInternal(registryObjects, checkReference, response);

	}

	private ResponseContext createOrVersionInternal(List<RegistryObjectType> registryObjects, boolean checkReference, ResponseContext response) {

		ReadManager rm = ReadManager.getInstance();

		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {

			boolean result = false;			
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
					result = version(graphDB, node, registryObject, checkReference, response);
					
				} else {					
					// create a node within database for at least a registry object
					result = create(graphDB, registryObject, checkReference, response);
					
				}
				
				// in case of a failure, the respective request
				// is terminated and the fill error message sent back
				if (result == false) break;

			}
			
			if (result == true) {

				// unless success() is invoked, the transaction will fail upon finish()
				tx.success();

				// this is a successful request, we therefore indicate this status in the registry's response
				response.setStatus(CanonicalSchemes.CANONICAL_RESPONSE_STATUS_TYPE_ID_Success);
			
			}			
			
		} finally {
			tx.finish();
		}
		
		// return response
		return response;

	}
	
	private Set<RegistryObjectType> catalogObjects(List<Node> nodes, CatalogerPlugin cataloger) throws Exception {
		
		ReadManager rm = ReadManager.getInstance();
		
		Set<RegistryObjectType> catalogedObjects = new HashSet<RegistryObjectType>();
		for (Node node:nodes) {

			String language = null;
			RegistryObjectType registryObject = (RegistryObjectType)rm.toBinding(node, language);

			catalogedObjects.addAll(cataloger.catalogObject(registryObject));
		}
		
		return catalogedObjects;
		
	}
	

	// this helper method partitions (2) target objects into a multiple
	// set of nodes distinguished by their objectType attribute
	
	private Map<String,List<Node>> partitionNodes(List<ObjectRefType> objectRefs) {
		
		ReadManager rm = ReadManager.getInstance();
		Map<String,List<Node>> partitionedNodes = new HashMap<String, List<Node>>();
		
		for (ObjectRefType objectRef:objectRefs) {
			
			Node node = rm.findNodeByID(objectRef.getId());
			if (node == null) continue;
			
			String objectType = null;
			if (node.hasProperty(NEOBase.OASIS_RIM_TYPE)) objectType = (String)node.getProperty(NEOBase.OASIS_RIM_TYPE);
		
			if (objectType == null) continue;
			
			if (partitionedNodes.get(objectType) == null) partitionedNodes.put(objectType, new ArrayList<Node>());
			partitionedNodes.get(objectType).add(node);

		}
		
		return partitionedNodes;
		
	}
	
	// this method expects that no node with the unique identifier provided
	// with the registryObject exists in the database
	
	private boolean create(EmbeddedGraphDatabase graphDB, RegistryObjectType registryObject, Boolean checkReference, ResponseContext response) {

		try {

			// create new node within database
			Node node = toNode(graphDB, registryObject, checkReference);
			response.addCreated(node);
			
			return true;
			
		} catch (Exception e) {
			response.addException(e);
		
		}
		
		return false;
		
	}

	private boolean replace(EmbeddedGraphDatabase graphDB, Node node, RegistryObjectType registryObject, Boolean checkReference, ResponseContext response) {

		try {

			// replace existing node within database
			node = fillNode(graphDB, node, registryObject, checkReference);
			response.addUpdated(node);

			return true;
			
		} catch (Exception e) {
			response.addException(e);
			
		}
		
		return false;

	}

	private boolean update(EmbeddedGraphDatabase graphDB, Node node, Boolean checkReference, List<UpdateActionType> updateActions, ResponseContext response) {
		
		boolean result = false;
		
		try {

			updateNode(graphDB, node, checkReference, updateActions);
			response.addUpdated(node);

			return true;
			
		} catch (Exception e) {
			response.addException(e);

		}

		return result;
		
	}
	
	// this version method is used for createOrVersion requests
	
	private boolean version(EmbeddedGraphDatabase graphDB, Node node, RegistryObjectType registryObject, Boolean checkReference, ResponseContext response) {
		
		try {

			Node target = versionNode(graphDB, node, registryObject, checkReference);
			response.addCreated(target);

			return true;
			
		} catch (Exception e) {
			response.addException(e);

		}

		return false;
		
	}

	// this version method is used for updateAndVersion requests
	
	private Node version(EmbeddedGraphDatabase graphDB, Node node, Boolean checkReference, ResponseContext response) {
		
		Node target = null;

		try {
			target = versionNode(graphDB, node, null, checkReference);
			
		} catch (Exception e) {
			response.addException(e);

		}
		
		return target;

	}

	
	private boolean delete(Node node, Boolean checkReference, Boolean deleteChildren, String deletionScope, ResponseContext response) {

			try {

			removeNode(node, checkReference, deleteChildren, deletionScope);
			response.addDeleted(node);
			
			return true;
			
		} catch (Exception e) {
			response.addException(e);

		}

		return false;
		
	}

	/*
	 * If an object does not exist, server MUST return ObjectNotFoundException. If an object 
	 * already exists, server MUST update the existing object without creating a new version.	
	 */

	private ResponseContext updateOnly(UpdateRequestContext request, ResponseContext response) {

		ReadManager rm = ReadManager.getInstance();

		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {

			boolean result = false;

			Boolean checkReference = request.isCheckReference();
			
			List<ObjectRefType> objectRefs = request.getList();
			List<UpdateActionType> updateActions = request.getUpdateActions();
			
			for (ObjectRefType objectRef:objectRefs) {
				
				Node node = null;
				
				// determine whether the respective registry object already exists
				String nid = objectRef.getId();
				
				// __DESIGN__
				
				// it is expected, that the registry object is provided with a unique identifier
				node = rm.findNodeByID(nid);
				if (node == null) {

					ObjectNotFoundException exception = new ObjectNotFoundException("[UpdateObjectsRequest] ObjectRefType node with id '" + nid + "' does not exist.");
					response.addException(exception);
					
				} else {

					result = update(graphDB, node, checkReference, updateActions, response);

					// in case of a failure, the respective request
					// is terminated and the error message sent back
					if (result == false) break;

				}
				
			}
			
			if (result == true) {

				// unless success() is invoked, the transaction will fail upon finish()
				tx.success();

				// this is a successful request, we therefore indicate this status in the registry's response
				response.setStatus(CanonicalSchemes.CANONICAL_RESPONSE_STATUS_TYPE_ID_Success);
			
			}
			
		} finally {
			tx.finish();
		}
		
		// return response
		return response;

	}

	/*
	 * If an object does not exist, server MUST return ObjectNotFoundException. If an object 
	 * already exists, server MUST create a new version of the existing object before applying 
	 * the requested update action.
	 */
	
	private ResponseContext updateAndVersion(UpdateRequestContext request, ResponseContext response) {

		ReadManager rm = ReadManager.getInstance();

		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {

			boolean result = false;

			Boolean checkReference = request.isCheckReference();
			
			List<ObjectRefType> objectRefs = request.getList();
			List<UpdateActionType> updateActions = request.getUpdateActions();
			
			for (ObjectRefType objectRef:objectRefs) {
				
				Node node = null;
				
				// determine whether the respective registry object already exists
				String nid = objectRef.getId();
				
				// __DESIGN__
				
				// it is expected, that the registry object is provided with a unique identifier
				node = rm.findNodeByID(nid);
				if (node == null) {

					ObjectNotFoundException exception = new ObjectNotFoundException("[UpdateObjectsRequest] ObjectRefType node with id '" + nid + "' does not exist.");
					response.addException(exception);
					
				} else {

					Node target = version(graphDB, node, checkReference, response);
					if (target != null) result = update(graphDB, target, checkReference, updateActions, response);

					// in case of a failure, the respective request
					// is terminated and the error message sent back
					if (result == false) break;

				}
				
			}
			
			if (result == true) {

				// unless success() is invoked, the transaction will fail upon finish()
				tx.success();

				// this is a successful request, we therefore indicate this status in the registry's response
				response.setStatus(CanonicalSchemes.CANONICAL_RESPONSE_STATUS_TYPE_ID_Success);
			
			}
			
		} finally {
			tx.finish();
		}
		
		// return response
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
	
	private void updateNode(EmbeddedGraphDatabase graphDB, Node node, Boolean checkReference, List<UpdateActionType> updateActions) throws Exception {	

		ReadManager rm = ReadManager.getInstance();

		// retrieve the object binding from the node provided
		String language = null;		
		Object binding = rm.toBinding(node, language);
		
		// update the binding of a certain node
		UpdateHandler up = UpdateHandler.getInstance();
		binding = up.updateBinding(binding, checkReference, updateActions);

		// finally fill the node from the modified binding

		// an update request inserts a new value, updates and existing
		// value or deletes an existing value
		
		// - insert or update
		//
		// independent of whether the respective parameter
		// exists in the node, the parameter is set to the
		// value provided

		// - delete
		//
		// 'delete' means that the respective parameter value is
		// set to null in the binding object; as fill first clears
		// all node paramaters and then update them from the binding
		// object, 'delete' is supported this way

		fillNode(graphDB, node, binding, checkReference);
		
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
	
	private Node versionNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws Exception {

		Node target = NEOBase.cloneAndVersionNode(graphDB, node);

		// index target object
		Database.getInstance().getNodeIndex().add(target, NEOBase.OASIS_RIM_ID, target.getProperty(NEOBase.OASIS_RIM_ID));			    
		
		// in case of an update request, the provided binding object may be null
		if (binding == null) return target;

		// in case of a versioning request, the VersionInfoType is excluded from
		// the fill request, as the respective information is already set with
		// cloneAndVersionNode
		
		boolean excludeVersion = true;
		
		String bindingName = (String)target.getProperty(NEOBase.NEO4J_TYPE);
		Class<?> clazz = NEOBase.getClassNEOByName(bindingName);

	    Method method = clazz.getMethod("fillNode", graphDB.getClass(), Node.class, Object.class, boolean.class, boolean.class);
	    method.invoke(null, graphDB, node, binding, checkReference, excludeVersion);

	    return target;
	    
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
