package de.kp.registry.server.neo4j.database;

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

import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.spi.CanonicalConstants;

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
	public RegistryResponseType submitObjects(List<RegistryObjectType> registryObjects, Boolean checkReference, Mode mode) {
		
		String modeValue = mode.value();
		if (modeValue.equals(Mode.CREATE_ONLY)) {

			/*
			 * If an object does not exist, server MUST create it as a new object. If an object
			 * already exists, the server MUST return an ObjectExistsException fault message
			 */
			return createOnly(registryObjects, checkReference);			
					
		} else if (modeValue.equals(Mode.CREATE_OR_REPLACE)) {
			
			/*
			 * If an object does not exist, server MUST create it as a new object.
			 * If an object already exists, server MUST replace the existing object 
			 * with the submitted object
			 */
			return createOrReplace(registryObjects, checkReference);			
			
		} else if (modeValue.equals(Mode.CREATE_OR_VERSION)) {
			
			/*
			 * If an object does not exist, server MUST create it as a new object. If an object
			 * already exists, server MUST not alter the existing object and instead it MUST create
			 * a new version of the existing object using the state of the submitted object
			 */
			return createOrVersion(registryObjects, checkReference);			
			
		}

		return null;
		
	}
	
	// this public method is used by the LifecycleManager
	public RegistryResponseType removeObjects(List<ObjectRefType> objectRefs, Boolean checkReference, Boolean deleteChildren, String deletionScope) {	

		ReadManager rm = ReadManager.getInstance();
		
		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		RegistryResponseType registryResponse = createResponse();
		
		try {

			for (ObjectRefType objectRef:objectRefs) {

				Node node = null;
				
				// determine whether the respective registry object already exists
				String id = objectRef.getId();
				
				// __DESIGN__
				
				// it is expected, that the registry object is provided with a unique identifier
				node = rm.findNodeByID(id);
				if (node != null) {

					boolean result = delete(node, checkReference, deleteChildren, deletionScope, registryResponse);
					if (result == false) {
						// TODO
					}

				} else {

					if (checkReference == true) {
					
						// Specifies that a server MUST check objects being removed AND make sure 
						// that there are no references to them from other objects via reference 
						// attributes and slots. If a reference exists then the server MUST return
						// ReferencesExistsException			
						
					}
				}

			}
			
			tx.success();
			// this is a successful request, we therefore indicate this status in the registry's response
			registryResponse.setStatus(CanonicalConstants.SUCCESS);

		} finally {
			tx.finish();
		}

		return registryResponse;

	}
	
	public RegistryResponseType updateObjects(List<ObjectRefType> registryObjects, Boolean checkReference, Mode mode, List<UpdateActionType> updateActions) {
		return null;
	}
	
	// private methods to support the submitObjects request
	private RegistryResponseType createOnly(List<RegistryObjectType> registryObjects, Boolean checkReference) {

		ReadManager rm = ReadManager.getInstance();

		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		RegistryResponseType registryResponse = createResponse();
		
		try {

			for (RegistryObjectType registryObject:registryObjects) {
				
				Node node = null;
				
				// determine whether the respective registry object already exists
				String id = registryObject.getId();
				
				// __DESIGN__
				
				// it is expected, that the registry object is provided with a unique identifier
				node = rm.findNodeByID(id);
				if (node != null) {
					
					// If an object already exists, the server MUST return an 
					// ObjectExistsException fault message
				
					// this request failed, we therefore indicate this status in the registry's response
					registryResponse.setStatus(CanonicalConstants.FAILURE);

					// TODO: build response
					
				} else {
					
					boolean result = create(graphDB, registryObject, checkReference, registryResponse);
					if (result == false) {
						// TODO: build response
					}
				}
				
			}
			
			tx.success();
			// this is a successful request, we therefore indicate this status in the registry's response
			registryResponse.setStatus(CanonicalConstants.SUCCESS);
			
		} finally {
			tx.finish();
		}

		return registryResponse;
	}

	private RegistryResponseType createOrReplace(List<RegistryObjectType> registryObjects, Boolean checkReference) {

		ReadManager rm = ReadManager.getInstance();
		
		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		RegistryResponseType registryResponse = createResponse();
		
		try {

			for (RegistryObjectType registryObject:registryObjects) {
				
				Node node = null;
				
				// determine whether the respective registry object already exists
				String id = registryObject.getId();
				
				// __DESIGN__
				
				// it is expected, that the registry object is provided with a unique identifier
				node = rm.findNodeByID(id);
				if (node != null) {
					
					// if an object already exists, the server MUST replace the existing object with the submitted object
					boolean result = replace(graphDB, node, registryObject, checkReference, registryResponse);
					if (result == false) {
						// TODO
					}
					
				} else {

					// create a node within database for at least a registry object
					boolean result = create(graphDB, registryObject, checkReference, registryResponse);
					if (result == false) {
						// TODO
					}

				}
				
			}
			
			tx.success();

			// this is a successful request, we therefore indicate this status in the registry's response
			registryResponse.setStatus(CanonicalConstants.SUCCESS);
			
		} finally {
			tx.finish();
		}

		return registryResponse;
		
	}

	private RegistryResponseType createOrVersion(List<RegistryObjectType> registryObjects, Boolean checkReference) {

		ReadManager rm = ReadManager.getInstance();

		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		RegistryResponseType registryResponse = createResponse();
		
		try {

			for (RegistryObjectType registryObject:registryObjects) {
				
				Node node = null;
				
				// determine whether the respective registry object already exists
				String id = registryObject.getId();
				
				// __DESIGN__
				
				// it is expected, that the registry object is provided with a unique identifier
				node = rm.findNodeByID(id);
				if (node != null) {
					
					// If an object already exists, server MUST not alter the existing 
					// object and instead it MUST create a new version of the existing 
					// object using the state of the submitted object
					version(node, registryObject, checkReference, registryResponse);
					
				} else {
					
					// create a node within database for at least a registry object
					boolean result = create(graphDB, registryObject, checkReference, registryResponse);
					if (result == false) {
						// TODO
					}
					
				}

			}
			
			tx.success();
			
			// this is a successful request, we therefore indicate this status in the registry's response
			registryResponse.setStatus(CanonicalConstants.SUCCESS);
			
			
		} finally {
			tx.finish();
		}

		return registryResponse;

	}

	// this method expects that no node with the unique identifier provided
	// with the registryObject exists in the database
	
	private boolean create(EmbeddedGraphDatabase graphDB, RegistryObjectType registryObject, Boolean checkReference, RegistryResponseType response) {
		
		Node registryObjectTypeNode;

		try {
			// create new node within database
			registryObjectTypeNode = toNode(graphDB, registryObject, checkReference);

			String id = (String)registryObjectTypeNode.getProperty(NEOBase.OASIS_RIM_ID);
			addIdToResponse(id, response);

			return true;
			
		} catch (Exception e) {
			
			// this request failed, we therefore indicate this status in the registry's response
			response.setStatus(CanonicalConstants.FAILURE);

			// TODO 
			e.printStackTrace();
		}
		
		return false;
		
	}

	private boolean replace(EmbeddedGraphDatabase graphDB, Node node, RegistryObjectType registryObject, Boolean checkReference, RegistryResponseType response) {

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
			
			// this request failed, we therefore indicate this status in the registry's response
			response.setStatus(CanonicalConstants.FAILURE);

			// TODO 
			e.printStackTrace();
		}
		
		return false;

	}

	private void version(Node node, RegistryObjectType registryObject, Boolean checkReference, RegistryResponseType response) {

		// in case of a successful version of this registry object, the 
		// respective unique identifier is assigned to the registry response

	}

	private boolean delete(Node node, Boolean checkReference, Boolean deleteChildren, String deletionScope, RegistryResponseType response) {
		
		try {

			String id = (String)node.getProperty(NEOBase.OASIS_RIM_ID);
			removeNode(node, checkReference, deleteChildren, deletionScope);
			
			addIdToResponse(id, response);

			return true;
			
		} catch (Exception e) {
			
			// this request failed, we therefore indicate this status in the registry's response
			response.setStatus(CanonicalConstants.FAILURE);

			// TODO 
			e.printStackTrace();
		}

		return false;
		
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
	
	private RegistryResponseType createResponse() {

		
		RegistryResponseType registryResponseType = ebRSFactory.createRegistryResponseType();
		
		// - REQUEST-ID
		
		// This attribute contains the id of the request that returned this QueryResponse;
		// it is not set for submitObjects request

		return registryResponseType;
		
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
