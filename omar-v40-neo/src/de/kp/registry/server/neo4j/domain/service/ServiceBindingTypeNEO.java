package de.kp.registry.server.neo4j.domain.service;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ServiceBindingType;

import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

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
		
		node = clearNode(node);
		return fillNodeInternal(graphDB, node, binding, checkReference); 
	
	}

	public static Node clearNode(Node node) {

		// clear the RegistryObjectType of the respective node
		node = RegistryObjectTypeNEO.clearNode(node);
		
		// - SERVICE-INTERFACE (0..1)
		if (node.hasProperty(OASIS_RIM_SERVICE_INTERFACE)) node.removeProperty(OASIS_RIM_SERVICE_INTERFACE);
		
		return node;
		
	}

	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		
		// the parameter 'checkReference' is not explicitly evaluated by ServiceBindingType

		ServiceBindingType serviceBindingType = (ServiceBindingType)binding;
		
		// - SERVICE-INTERFACE (0..1)
		String serviceInterface = serviceBindingType.getServiceInterface();

		// ===== FILL NODE =====

		// - SERVICE-INTERFACE (0..1)
		if (serviceInterface != null) node.setProperty(OASIS_RIM_SERVICE_INTERFACE, serviceInterface);

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
