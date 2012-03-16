package de.kp.registry.server.neo4j.domain.query;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.QueryType;

import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class QueryTypeNEO extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		QueryType queryType = (QueryType)binding;
		
		// - QUERY-DEFINITION (1..1)
		String queryDefinition = queryType.getQueryDefinition();
		
		// create node from underlying ExtensibleObjectType
		Node queryTypeNode = ExtensibleObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a QueryType
		queryTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - QUERY-DEFINITION (1..1)
		queryTypeNode.setProperty(OASIS_RIM_QUERY_DEFINITION, queryDefinition);

		return queryTypeNode;
	}

	public static Node clearNode(Node node, boolean excludeVersion) {
		
		// - QUERY-DEFINITION (1..1)
		node.removeProperty(OASIS_RIM_QUERY_DEFINITION);
		return node;

	}

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear QueryType specific parameters
		node = clearNode(node, false);
		
		// clear node from ExtensibleObjectType specific parameters and remove
		ExtensibleObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}

	public static Object toBinding(Node node) {
		return toBinding(node, null);
	}

	public static Object toBinding(Node node, String language) {
	
		QueryType binding = factory.createQueryType();
		binding = (QueryType)ExtensibleObjectTypeNEO.fillBinding(node, binding, language);

		// - QUERY-DEFINITION (1..1)
		binding.setQueryDefinition((String)node.getProperty(OASIS_RIM_QUERY_DEFINITION));
		
		return binding;
		
	}
	
	public static String getNType() {
		return "QueryType";
	}
}
