package de.kp.registry.server.neo4j.domain.core;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;

import de.kp.registry.server.neo4j.database.Database;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class ObjectRefTypeNEO extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		ObjectRefType objectRefType = (ObjectRefType)binding;
		
		// - ID (1..1)
		String id = objectRefType.getId();
		
		// create node from underlying ExtensibleObjectType
		Node objectRefTypeNode = ExtensibleObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe an ObjectRefType
		objectRefTypeNode.setProperty(NEO4J_TYPE, getNType());
		
		// - ID (1..1)
		objectRefTypeNode.setProperty(OASIS_RIM_ID, id);
		
		// add node to node index 
		Database.getInstance().getNodeIndex().add(objectRefTypeNode, OASIS_RIM_ID, id);
		
		return objectRefTypeNode;
		
	}

	// this is a common wrapper to delete an ObjectRefType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {		
		ExtensibleObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
	}

	public static Object toBinding(Node node) {
		return fillBinding(node, factory.createObjectRefType());
	}
	
	public static Object fillBinding(Node node, Object binding) {
		
		ObjectRefType objectRefType = (ObjectRefType)binding;

		// - ID (1..1)
		objectRefType.setId((String)node.getProperty(OASIS_RIM_ID));
		
		return objectRefType;

	}
	
	public static String getNType() {
		return "ObjectRefType";
	}
	
}
