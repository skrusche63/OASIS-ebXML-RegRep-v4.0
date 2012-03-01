package de.kp.registry.server.neo4j.domain.service;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ServiceBindingType;

import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;

public class ServiceBindingTypeNEO extends RegistryObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		ServiceBindingType serviceBindingType = (ServiceBindingType)binding;
		
		// - SERVICE-INTERFACE (0..1)
		String serviceInterface = serviceBindingType.getServiceInterface();
		
		// create node from underlying RegistryObjectType
		Node serviceBindingTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a ServiceBindingType
		serviceBindingTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - SERVICE-INTERFACE (0..1)
		if (serviceInterface != null) serviceBindingTypeNode.setProperty(OASIS_RIM_SERVICE_INTERFACE, serviceInterface);

		return serviceBindingTypeNode;
		
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
