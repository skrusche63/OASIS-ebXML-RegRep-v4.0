package de.kp.registry.server.neo4j.domain.service;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ServiceInterfaceType;

import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;

public class ServiceInterfaceTypeNEO extends RegistryObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {

		// create node from underlying RegistryObjectType
		Node serviceInterfaceTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a ServiceInterfaceType
		serviceInterfaceTypeNode.setProperty(NEO4J_TYPE, getNType());

		return serviceInterfaceTypeNode;
		
	}

	public static Object toBinding(Node node) {
		
		ServiceInterfaceType binding = factory.createServiceInterfaceType();
		binding = (ServiceInterfaceType)RegistryObjectTypeNEO.fillBinding(node, binding);
		
		return binding;
		
	}
	
	public static String getNType() {
		return "ServiceInterfaceType";
	}
}
