package de.kp.registry.server.neo4j.domain.query;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class StringQueryExpressionTypeNEO extends QueryExpressionTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		return null;
	}

	public static String getNType() {
		return "StringQueryExpressionType";
	}
}
