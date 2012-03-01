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
import org.oasis.ebxml.registry.bindings.rs.ObjectFactory;
import org.oasis.ebxml.registry.bindings.rs.RegistryResponseType;

import de.kp.registry.server.neo4j.domain.NEOBase;

public class WriteManager {

	private static WriteManager instance = new WriteManager();
	
	// reference to OASIS ebRS object factory
	public static ObjectFactory ebRSFactory = new ObjectFactory();

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
		return null;
	}
	
	public RegistryResponseType updateObjects(List<ObjectRefType> registryObjects, Boolean checkReference, Mode mode, List<UpdateActionType> updateActions) {
		return null;
	}
	
	// private methods to support the submitObjects request
	private RegistryResponseType createOnly(List<RegistryObjectType> registryObjects, Boolean checkReference) {

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
				node = ReadManager.getInstance().findNodeByID(id);
				if (node != null) {
					
					// If an object already exists, the server MUST return an 
					// ObjectExistsException fault message
				
					// TODO
					
				} else {
					create(registryObject, checkReference, registryResponse);
					
				}
				
			}
			
			tx.success();
			
		} finally {
			tx.finish();
		}

		return registryResponse;
	}

	private RegistryResponseType createOrReplace(List<RegistryObjectType> registryObjects, Boolean checkReference) {
		
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
				node = ReadManager.getInstance().findNodeByID(id);
				if (node != null) {
					
					// If an object already exists, server MUST replace the 
					// existing object with the submitted object
					replace(node, registryObject, checkReference, registryResponse);
					
				} else {
					create(registryObject, checkReference, registryResponse);
					
				}

			}
			
			tx.success();
			
		} finally {
			tx.finish();
		}

		return registryResponse;
		
	}

	private RegistryResponseType createOrVersion(List<RegistryObjectType> registryObjects, Boolean checkReference) {

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
				node = ReadManager.getInstance().findNodeByID(id);
				if (node != null) {
					
					// If an object already exists, server MUST not alter the existing 
					// object and instead it MUST create a new version of the existing 
					// object using the state of the submitted object
					version(node, registryObject, checkReference, registryResponse);
					
				} else {
					create(registryObject, checkReference, registryResponse);
					
				}

			}
			
			tx.success();
			
		} finally {
			tx.finish();
		}

		return registryResponse;

	}

	private void create(RegistryObjectType registryObject, Boolean checkReference, RegistryResponseType response) {
		
	}

	private void replace(Node node, RegistryObjectType registryObject, Boolean checkReference, RegistryResponseType response) {
		
	}

	private void version(Node node, RegistryObjectType registryObject, Boolean checkReference, RegistryResponseType response) {
		
	}

	private RegistryResponseType createResponse() {

		
		RegistryResponseType registryResponseType = ebRSFactory.createRegistryResponseType();
		
		// - REQUEST-ID
		
		// This attribute contains the id of the request that returned this QueryResponse;
		// it is not set for submitObjects request

		return registryResponseType;
		
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
		toNode(graphDB, binding);			
	}
	
	/**
	 * Generic wrapper rim binding -> node 
	 * 
	 * @param graphDB
	 * @param binding
	 * @return
	 * @throws Exception
	 */
	public Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {

		Class<?> clazz = NEOBase.getClassNEO(binding);

	    Method method = clazz.getMethod("toNode", graphDB.getClass(), Object.class);
	    return (Node) method.invoke(null, graphDB, binding);
    	
	}
}
