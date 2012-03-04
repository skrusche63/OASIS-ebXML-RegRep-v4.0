package de.kp.registry.server.neo4j.domain.service;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ServiceEndpointType;

import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class ServiceEndpointTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new ServiceEndpointType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		ServiceEndpointType serviceEndpointType = (ServiceEndpointType)binding;
		
		// - ADDRESS (0..1)
		String address = serviceEndpointType.getAddress();
		
		// - SERVICE-BINDING (0..1)
		String serviceBinding = serviceEndpointType.getServiceBinding();
		
		// create node from underlying RegistryObjectType
		Node serviceEndpointTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a ServiceEndpointType
		serviceEndpointTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - ADDRESS (0..1)
		if (address != null) serviceEndpointTypeNode.setProperty(OASIS_RIM_ADDRESS, address);
		
		// - SERVICE-BINDING (0..1)
		if (serviceBinding != null) serviceEndpointTypeNode.setProperty(OASIS_RIM_SERVICE_BINDING, serviceBinding);
		
		return serviceEndpointTypeNode;
		
	}
	
	// this method replaces an existing ServiceEndpointType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return null;
	}

	public static Node clearNode(Node node) {

		// clear the RegistryObjectType of the respective node
		node = RegistryObjectTypeNEO.clearNode(node);
		
		// TODO
		return null;
		
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
