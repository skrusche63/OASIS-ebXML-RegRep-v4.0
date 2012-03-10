package de.kp.registry.server.neo4j.domain.service;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ServiceEndpointType;

import de.kp.registry.server.neo4j.database.ReadManager;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.exception.UnresolvedReferenceException;

public class ServiceEndpointTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new ServiceEndpointType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		// create node from underlying RegistryObjectType
		Node node = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a ServiceEndpointType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);
		
	}
	
	// this method replaces an existing ServiceEndpointType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier

	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return fillNode(graphDB, node, binding, checkReference, false);
	}
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference, boolean excludeVersion) throws RegistryException {
		
		// clear ServiceEndpointType specific parameters
		node = clearNode(node, excludeVersion);

		// clear & fill node with RegistryObjectType specific parameters
		node = RegistryObjectTypeNEO.fillNode(graphDB, node, binding, checkReference, excludeVersion);
		
		// fill node with ServiceEndpointType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 
	
	}

	public static Node clearNode(Node node, boolean excludeVersion) {
		
		// - ADDRESS (0..1)
		if (node.hasProperty(OASIS_RIM_ADDRESS)) node.removeProperty(OASIS_RIM_ADDRESS);
		
		// - SERVICE-BINDING (0..1)
		if (node.hasProperty(OASIS_RIM_SERVICE_BINDING)) node.removeProperty(OASIS_RIM_SERVICE_BINDING);

		return node;
		
	}
	
	// this is a common wrapper to delete ServiceEndpointType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear ServiceEndpointType specific parameters
		node = clearNode(node, false);
		
		// clear node from RegistryObjectType specific parameters and remove
		RegistryObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}
	
	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		ServiceEndpointType serviceEndpointType = (ServiceEndpointType)binding;
		
		// - ADDRESS (0..1)
		String address = serviceEndpointType.getAddress();
		
		// - SERVICE-BINDING (0..1)
		String serviceBinding = serviceEndpointType.getServiceBinding();

		// ===== FILL NODE =====

		// - ADDRESS (0..1)
		if (address != null) node.setProperty(OASIS_RIM_ADDRESS, address);
		
		// - SERVICE-BINDING (0..1)
		if (serviceBinding != null) {
			
			if (checkReference == true) {
				// make sure that the ServiceBindingType references an existing node within the database
				if (ReadManager.getInstance().findNodeByID(serviceBinding) == null) 
					throw new UnresolvedReferenceException("[ServiceEndpointType] ServiceBindingType node with id '" + serviceBinding + "' does not exist.");		

			}
			
			node.setProperty(OASIS_RIM_SERVICE_BINDING, serviceBinding);
		}
		
		return node;

	}

	public static Object toBinding(Node node) {
	
		ServiceEndpointType binding = factory.createServiceEndpointType();
		binding = (ServiceEndpointType)RegistryObjectTypeNEO.fillBinding(node, binding);
		
		// - ADDRESS (0..1)
		if (node.hasProperty(OASIS_RIM_ADDRESS)) binding.setAddress((String)node.getProperty(OASIS_RIM_ADDRESS));

		// - SERVICE-BINDING (0..1)
		if (node.hasProperty(OASIS_RIM_SERVICE_BINDING)) binding.setServiceBinding((String)node.getProperty(OASIS_RIM_SERVICE_BINDING));
		
		return binding;
	}
	
	public static String getNType() {
		return "ServiceEndpointType";
	}
}
