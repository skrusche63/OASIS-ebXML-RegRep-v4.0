package de.kp.registry.server.neo4j.domain.query;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.QueryType;

import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class QueryTypeNEO extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws RegistryException {
		
		QueryType queryType = (QueryType)binding;
		
		// - QUERY-DEFINITION (1..1)
		String queryDefinition = queryType.getQueryDefinition();
		
		// create node from underlying ExtensibleObjectType
		Node queryTypeNode = ExtensibleObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a QueryType
		queryTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - QUERY-DEFINITION (1..1)
		queryTypeNode.setProperty(OASIS_RIM_QUERY_DEFINITION, queryDefinition);

		return queryTypeNode;
	}

	public static Object toBinding(Node node) {
	
		QueryType binding = factory.createQueryType();
		binding = (QueryType)ExtensibleObjectTypeNEO.fillBinding(node, binding);

		// - QUERY-DEFINITION (1..1)
		binding.setQueryDefinition((String)node.getProperty(OASIS_RIM_QUERY_DEFINITION));
		
		return binding;
		
	}
	
	public static String getNType() {
		return "QueryType";
	}
}
