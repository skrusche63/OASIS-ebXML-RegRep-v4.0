package de.kp.registry.server.neo4j.domain.query;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.StringQueryExpressionType;

public class StringQueryExpressionTypeNEO extends QueryExpressionTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		StringQueryExpressionType stringQueryExpressionType = (StringQueryExpressionType)binding;
		
		// - VALUE (1..1)
		String value = stringQueryExpressionType.getValue();
		
		// create node from underlying QueryExpressionType
		Node stringQueryExpressionTypeNode = QueryExpressionTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a StringQueryExpressionType
		stringQueryExpressionTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - VALUE (1..1)
		stringQueryExpressionTypeNode.setProperty(OASIS_RIM_QUERY_VALUE, value);
		
		return stringQueryExpressionTypeNode;
	}

	public static String getNType() {
		return "StringQueryExpressionType";
	}
}
