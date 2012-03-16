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
		Node node = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a ServiceInterfaceType
		node.setProperty(NEO4J_TYPE, getNType());

		return node;
		
	}

	// this method replaces an existing ServiceInterfaceType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier

	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return fillNode(graphDB, node, binding, checkReference, false);
	}

	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference, boolean excludeVersion) throws RegistryException {

		// clear ServiceInterfaceType specific parameters
		node = clearNode(node, excludeVersion);

		// clear & fill node with RegistryObjectType specific parameters
		node = RegistryObjectTypeNEO.fillNode(graphDB, node, binding, checkReference, excludeVersion);
		
		// fill node with ServiceInterfaceType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 
	}

	public static Node clearNode(Node node, boolean excludeVersion) {
		return node;
	}

	// this is a common wrapper to delete ServiceInterfaceType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear ServiceInterfaceType specific parameters
		node = clearNode(node, false);
		
		// clear node from RegistryObjectType specific parameters and remove
		RegistryObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}
	
	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return node;
	}

	public static Object toBinding(Node node) {
		return toBinding(node, null);
	}

	public static Object toBinding(Node node, String language) {
		
		ServiceInterfaceType binding = factory.createServiceInterfaceType();
		binding = (ServiceInterfaceType)RegistryObjectTypeNEO.fillBinding(node, binding, language);
		
		return binding;
		
	}
	
	public static String getNType() {
		return "ServiceInterfaceType";
	}
}
