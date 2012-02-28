package de.kp.registry.server.neo4j.domain.query;

import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ParameterType;
import org.oasis.ebxml.registry.bindings.rim.QueryDefinitionType;
import org.oasis.ebxml.registry.bindings.rim.QueryExpressionType;
import org.oasis.ebxml.registry.bindings.rim.StringQueryExpressionType;
import org.oasis.ebxml.registry.bindings.rim.XMLQueryExpressionType;

import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;

public class QueryDefinitionTypeNEO extends RegistryObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		QueryDefinitionType queryDefinitionType = (QueryDefinitionType)binding;
		
		// - PARAMETER (0..*)
		List<ParameterType> parameters = queryDefinitionType.getParameter();
		
		// - QUERY-EXPRESSION (0..1)
		QueryExpressionType queryExpression = queryDefinitionType.getQueryExpression();
				
		// create node from underlying RegistryObjectType
		Node queryDefinitionTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a QueryDefinitionType
		queryDefinitionTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - PARAMETER (0..*)
		if (parameters.isEmpty() == false) {
			
			for (ParameterType parameter:parameters) {
				
				Node parameterTypeNode = ParameterTypeNEO.toNode(graphDB, parameter);
				queryDefinitionTypeNode.createRelationshipTo(parameterTypeNode, RelationTypes.hasParameter);

			}
		}
		
		// - QUERY-EXPRESSION (0..1)
		if (queryExpression != null) {

			if (queryExpression instanceof StringQueryExpressionType) {
				
				Node queryExpressionTypeNode = StringQueryExpressionTypeNEO.toNode(graphDB, queryExpression);
				queryDefinitionTypeNode.createRelationshipTo(queryExpressionTypeNode, RelationTypes.hasQueryExpression);
				
			} else if (queryExpression instanceof XMLQueryExpressionType) {
				
				Node queryExpressionTypeNode = XMLQueryExpressionTypeNEO.toNode(graphDB, queryExpression);
				queryDefinitionTypeNode.createRelationshipTo(queryExpressionTypeNode, RelationTypes.hasQueryExpression);
				
			}
			
		}

		return queryDefinitionTypeNode;
	}

	public static String getNType() {
		return "QueryDefinitionType";
	}
}
