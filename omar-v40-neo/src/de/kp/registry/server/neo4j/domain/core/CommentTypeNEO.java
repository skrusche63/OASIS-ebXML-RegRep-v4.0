package de.kp.registry.server.neo4j.domain.core;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.CommentType;

import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class CommentTypeNEO extends ExtrinsicObjectTypeNEO {

	// this method creates a new CommentType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		// create node from underlying ExtrinsicObjectType
		Node commentTypeNode = ExtrinsicObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a CommentType
		commentTypeNode.setProperty(NEO4J_TYPE, getNType());

		return commentTypeNode;
	}

	// this method replaces an existing CommentType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {		
		return null;
	}

	public static Node clearNode(Node node) {
		return ExtrinsicObjectTypeNEO.clearNode(node);
	}

	public static Object toBinding(Node node) {
		
		CommentType binding = factory.createCommentType();
		binding = (CommentType)ExtrinsicObjectTypeNEO.fillBinding(node, binding);
		
		return binding;
		
	}
	
	public static String getNType() {
		return "CommentType";
	}
}
