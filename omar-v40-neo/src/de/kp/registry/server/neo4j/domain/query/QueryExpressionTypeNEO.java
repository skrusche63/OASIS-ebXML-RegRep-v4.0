package de.kp.registry.server.neo4j.domain.query;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.QueryExpressionType;

import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class QueryExpressionTypeNEO  extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		QueryExpressionType queryExpressionType = (QueryExpressionType)binding;
		
		// - QUERY-LANGUAGE (1..1)
		String queryLanguage = queryExpressionType.getQueryLanguage();
		
		// create node from underlying ExtensibleObjectType
		Node queryExpressionTypeNode = ExtensibleObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a QueryExpressionType
		queryExpressionTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - QUERY-LANGUAGE (1..1)
		queryExpressionTypeNode.setProperty(OASIS_RIM_QUERY_LANGUAGE, queryLanguage);
		
		return queryExpressionTypeNode;
	}

	public static Node clearNode(Node node) {
		
		// - QUERY-LANGUAGE (1..1)
		node.removeProperty(OASIS_RIM_QUERY_LANGUAGE);
		return node;

	}

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear QueryExpressionType specific parameters
		node = clearNode(node);
		
		// clear node from ExtensibleObjectType specific parameters and remove
		ExtensibleObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}
	
	public static Object fillBinding(Node node, Object binding) {
		
		QueryExpressionType queryExpressionType = (QueryExpressionType)ExtensibleObjectTypeNEO.fillBinding(node, binding);

		// - QUERY-LANGUAGE (1..1)
		queryExpressionType.setQueryLanguage((String)node.getProperty(OASIS_RIM_QUERY_LANGUAGE));
		
		return queryExpressionType;
		
	}
	
	public static String getNType() {
		return "QueryExpressionType";
	}
}
