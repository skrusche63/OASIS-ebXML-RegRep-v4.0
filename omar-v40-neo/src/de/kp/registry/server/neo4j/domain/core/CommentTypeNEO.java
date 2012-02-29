package de.kp.registry.server.neo4j.domain.core;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.CommentType;

public class CommentTypeNEO extends ExtrinsicObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		// create node from underlying ExtrinsicObjectType
		Node commentTypeNode = ExtrinsicObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a CommentType
		commentTypeNode.setProperty(NEO4J_TYPE, getNType());

		return commentTypeNode;
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
