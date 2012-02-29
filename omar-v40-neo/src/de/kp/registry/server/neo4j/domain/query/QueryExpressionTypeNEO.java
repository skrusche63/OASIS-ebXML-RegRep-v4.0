package de.kp.registry.server.neo4j.domain.query;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.QueryExpressionType;

import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;

public class QueryExpressionTypeNEO  extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		QueryExpressionType queryExpressionType = (QueryExpressionType)binding;
		
		// - QUERY-LANGUAGE (1..1)
		String queryLanguage = queryExpressionType.getQueryLanguage();
		
		// create node from underlying ExtensibleObjectType
		Node queryExpressionTypeNode = ExtensibleObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a QueryExpressionType
		queryExpressionTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - QUERY-LANGUAGE (1..1)
		queryExpressionTypeNode.setProperty(OASIS_RIM_QUERY_LANGUAGE, queryLanguage);
		
		return queryExpressionTypeNode;
	}

	public static String getNType() {
		return "QueryExpressionType";
	}
}
