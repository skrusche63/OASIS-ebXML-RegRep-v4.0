package de.kp.registry.server.neo4j.domain.query;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;

public class QueryTypeNEO extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		return null;
	}

	public static String getNType() {
		return "QueryType";
	}
}
