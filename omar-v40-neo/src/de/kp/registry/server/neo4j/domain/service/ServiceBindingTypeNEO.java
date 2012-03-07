package de.kp.registry.server.neo4j.domain.service;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ServiceBindingType;

import de.kp.registry.server.neo4j.database.ReadManager;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.exception.UnresolvedReferenceException;

public class ServiceBindingTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new ServiceBindingType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		// create node from underlying RegistryObjectType
		Node node = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a ServiceBindingType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);
		
	}

	// this method replaces an existing ServiceBindingType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		
		// clear ServiceBindingType specific parameters
		node = clearNode(node);
		
		// clear & fill node with RegistryObjectType specific parameters
		node = RegistryObjectTypeNEO.fillNode(graphDB, node, binding, checkReference);
		
		// fill node with ServiceBindingType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 
	
	}

	public static Node clearNode(Node node) {
		
		// - SERVICE-INTERFACE (0..1)
		if (node.hasProperty(OASIS_RIM_SERVICE_INTERFACE)) node.removeProperty(OASIS_RIM_SERVICE_INTERFACE);		
		return node;
		
	}

	// this is a common wrapper to delete ServiceBindingType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear ServiceBindingType specific parameters
		node = clearNode(node);
		
		// clear node from RegistryObjectType specific parameters and remove
		RegistryObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}

	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		
		// the parameter 'checkReference' is not explicitly evaluated by ServiceBindingType

		ServiceBindingType serviceBindingType = (ServiceBindingType)binding;
		
		// - SERVICE-INTERFACE (0..1)
		String serviceInterface = serviceBindingType.getServiceInterface();

		// ===== FILL NODE =====

		// - SERVICE-INTERFACE (0..1)
		if (serviceInterface != null) {
			
			if (checkReference == true) {
				// make sure that the ServiceInterfaceType references an existing node within the database
				if (ReadManager.getInstance().findNodeByID(serviceInterface) == null) 
					throw new UnresolvedReferenceException("[ServiceBindingType] ServiceInterfaceType node with id '" + serviceInterface + "' does not exist.");		

			}

			node.setProperty(OASIS_RIM_SERVICE_INTERFACE, serviceInterface);
		}

		return node;

	}

	public static Object toBinding(Node node) {
		
		ServiceBindingType binding = factory.createServiceBindingType();
		binding = (ServiceBindingType)RegistryObjectTypeNEO.fillBinding(node, binding);
		
		// - SERVICE-INTERFACE (0..1)
		if (node.hasProperty(OASIS_RIM_SERVICE_INTERFACE)) binding.setServiceInterface((String)node.getProperty(OASIS_RIM_SERVICE_INTERFACE));

		return binding;
		
	}
	
	public static String getNType() {
		return "ServiceBindingType";
	}
}
