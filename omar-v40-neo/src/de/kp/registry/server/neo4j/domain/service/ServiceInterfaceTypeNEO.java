package de.kp.registry.server.neo4j.domain.service;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ServiceInterfaceType;

import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class ServiceInterfaceTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new ServiceInterfaceType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {

		// create node from underlying RegistryObjectType
		Node serviceInterfaceTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a ServiceInterfaceType
		serviceInterfaceTypeNode.setProperty(NEO4J_TYPE, getNType());

		return serviceInterfaceTypeNode;
		
	}

	// this method replaces an existing ServiceInterfaceType node in the database
	
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
		
		ServiceInterfaceType binding = factory.createServiceInterfaceType();
		binding = (ServiceInterfaceType)RegistryObjectTypeNEO.fillBinding(node, binding);
		
		return binding;
		
	}
	
	public static String getNType() {
		return "ServiceInterfaceType";
	}
}
