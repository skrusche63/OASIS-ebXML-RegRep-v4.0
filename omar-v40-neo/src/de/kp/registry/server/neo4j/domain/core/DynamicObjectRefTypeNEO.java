package de.kp.registry.server.neo4j.domain.core;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.DynamicObjectRefType;
import org.oasis.ebxml.registry.bindings.rim.QueryType;

import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.query.QueryTypeNEO;

public class DynamicObjectRefTypeNEO extends ObjectRefTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		DynamicObjectRefType dynamicObjectRefType = (DynamicObjectRefType)binding;
		
		// - QUERY (1..1)
		QueryType query = dynamicObjectRefType.getQuery();

		// create node from underlying ObjectRefType
		Node dynamicObjectRefTypeNode = ExtensibleObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a DynamicObjectRefType
		dynamicObjectRefTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - QUERY (1..1)
		Node queryTypeNode = QueryTypeNEO.toNode(graphDB, query);
		dynamicObjectRefTypeNode.createRelationshipTo(queryTypeNode, RelationTypes.hasQuery);

		return dynamicObjectRefTypeNode;
	}

	public static String getNType() {
		return "DynamicObjectRefType";
	}
	
}
