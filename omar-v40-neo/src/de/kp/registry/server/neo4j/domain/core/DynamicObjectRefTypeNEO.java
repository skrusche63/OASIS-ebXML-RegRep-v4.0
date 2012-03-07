package de.kp.registry.server.neo4j.domain.core;

import java.util.Iterator;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.DynamicObjectRefType;
import org.oasis.ebxml.registry.bindings.rim.QueryType;

import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.query.QueryTypeNEO;

public class DynamicObjectRefTypeNEO extends ObjectRefTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		DynamicObjectRefType dynamicObjectRefType = (DynamicObjectRefType)binding;
		
		// - QUERY (1..1)
		QueryType query = dynamicObjectRefType.getQuery();

		// create node from underlying ObjectRefType
		Node dynamicObjectRefTypeNode = ExtensibleObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a DynamicObjectRefType
		dynamicObjectRefTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - QUERY (1..1)
		Node queryTypeNode = QueryTypeNEO.toNode(graphDB, query, checkReference);
		dynamicObjectRefTypeNode.createRelationshipTo(queryTypeNode, RelationTypes.hasQuery);

		return dynamicObjectRefTypeNode;
	}

	public static Node clearNode(Node node) {
		
		// -QUERY (1..1)

		// clear relationship and referenced QueryType node
		node = NEOBase.clearRelationship(node, RelationTypes.hasQuery, true);

		return node;
		
	}

	// this is a common wrapper to delete DynamicObjectRefType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear DynamicObjectRefType specific parameters
		node = clearNode(node);
		
		// clear node from ObjectRefType specific parameters and remove
		ObjectRefTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}

	public static Object toBinding(Node node) {
		
		DynamicObjectRefType binding = factory.createDynamicObjectRefType();

		// - QUERY (1..1)
		Iterable<Relationship> relationships = node.getRelationships(RelationTypes.hasQuery);
		if (relationships != null) {
		
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node queryTypeNode = relationship.getEndNode();
				
				QueryType queryType = (QueryType)QueryTypeNEO.toBinding(queryTypeNode);				
				binding.setQuery(queryType);

			}
			
		}
		
		return binding;
		
	}
	
	public static String getNType() {
		return "DynamicObjectRefType";
	}
	
}
