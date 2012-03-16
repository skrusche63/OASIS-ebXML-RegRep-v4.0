package de.kp.registry.server.neo4j.domain.query;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.StringQueryExpressionType;

import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class StringQueryExpressionTypeNEO extends QueryExpressionTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		StringQueryExpressionType stringQueryExpressionType = (StringQueryExpressionType)binding;
		
		// - VALUE (1..1)
		String value = stringQueryExpressionType.getValue();
		
		// create node from underlying QueryExpressionType
		Node stringQueryExpressionTypeNode = QueryExpressionTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a StringQueryExpressionType
		stringQueryExpressionTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - VALUE (1..1)
		stringQueryExpressionTypeNode.setProperty(OASIS_RIM_QUERY_VALUE, value);
		
		return stringQueryExpressionTypeNode;
	}

	public static Node clearNode(Node node, boolean excludeVersion) {
		
		// - OBJECT (1..1)
		node.removeProperty(OASIS_RIM_QUERY_VALUE);
		return node;

	}

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear StringQueryExpressionType specific parameters
		node = clearNode(node, false);
		
		// clear node from QueryExpressionType specific parameters and remove
		QueryExpressionTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}

	public static Object toBinding(Node node) {
		return toBinding(node, null);
	}

	public static Object toBinding(Node node, String language) {
		
		StringQueryExpressionType binding = factory.createStringQueryExpressionType();
		binding = (StringQueryExpressionType)QueryExpressionTypeNEO.fillBinding(node, binding, language);

		// - VALUE (1..1)
		binding.setValue((String)node.getProperty(OASIS_RIM_QUERY_VALUE));
		
		return binding;
		
	}
	
	public static String getNType() {
		return "StringQueryExpressionType";
	}
}
