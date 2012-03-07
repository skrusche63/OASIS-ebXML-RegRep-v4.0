package de.kp.registry.server.neo4j.domain.query;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ParameterType;
import org.oasis.ebxml.registry.bindings.rim.QueryDefinitionType;
import org.oasis.ebxml.registry.bindings.rim.QueryExpressionType;
import org.oasis.ebxml.registry.bindings.rim.StringQueryExpressionType;
import org.oasis.ebxml.registry.bindings.rim.XMLQueryExpressionType;

import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class QueryDefinitionTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new QueryDefinitionType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		// create node from underlying RegistryObjectType
		Node node = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a QueryDefinitionType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);

	}

	// this method replaces an existing QueryDefinitionType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		
		// clear QueryDefinitionType specific parameters
		node = clearNode(node);
		
		// clear & fill node with RegistryObjectType specific parameters
		node = RegistryObjectTypeNEO.fillNode(graphDB, node, binding, checkReference);
		
		// fill node with QueryDefinitionType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 

	}

	// TODO: we have to check whether deleting a certain node
	// also deletes its relationships and depending nodes
	
	// is it a cascading delete?
	
	public static Node clearNode(Node node) {
		
		// - PARAMETER (0..*)

		// __DESIGN__
		
		// ParameterType nodes are an intrinsic part of a QueryDefinitionType
		// and are therefore removed in addition to the respective relationships
		
		// clear relationship and referenced ParameterType nodes
		node = NEOBase.clearRelationship(node, RelationTypes.hasParameter, true);

		// - QUERY-EXPRESSION (0..1)

		// QueryExpressionType node is an intrinsic part of a QueryDefinitionType
		// and are therefore removed in addition to the respective relationships
		
		// clear relationship and referenced QueryExpressionType node
		node = NEOBase.clearRelationship(node, RelationTypes.hasQueryExpression, true);

		return node;
		
	}

	// TODO: checkReference
	
	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		QueryDefinitionType queryDefinitionType = (QueryDefinitionType)binding;
		
		// - PARAMETER (0..*)
		List<ParameterType> parameters = queryDefinitionType.getParameter();
		
		// - QUERY-EXPRESSION (0..1)
		QueryExpressionType queryExpression = queryDefinitionType.getQueryExpression();

		// ===== FILL NODE =====

		// - PARAMETER (0..*)
		if (parameters.isEmpty() == false) {
			
			for (ParameterType parameter:parameters) {
				
				Node parameterTypeNode = ParameterTypeNEO.toNode(graphDB, parameter, checkReference);
				node.createRelationshipTo(parameterTypeNode, RelationTypes.hasParameter);

			}
		}
		
		// - QUERY-EXPRESSION (0..1)
		if (queryExpression != null) {

			if (queryExpression instanceof StringQueryExpressionType) {
				
				Node queryExpressionTypeNode = StringQueryExpressionTypeNEO.toNode(graphDB, queryExpression, checkReference);
				node.createRelationshipTo(queryExpressionTypeNode, RelationTypes.hasQueryExpression);
				
			} else if (queryExpression instanceof XMLQueryExpressionType) {
				
				Node queryExpressionTypeNode = XMLQueryExpressionTypeNEO.toNode(graphDB, queryExpression, checkReference);
				node.createRelationshipTo(queryExpressionTypeNode, RelationTypes.hasQueryExpression);
				
			}
			
		}

		return node;

	}

	public static Object toBinding(Node node) {
		
		QueryDefinitionType binding = factory.createQueryDefinitionType();
		binding = (QueryDefinitionType)RegistryObjectTypeNEO.fillBinding(node, binding);

		Iterable<Relationship> relationships = null;

		// - PARAMETER (0..*)
		relationships = node.getRelationships(RelationTypes.hasParameter);
		if (relationships != null) {
		
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node parameterTypeNode = relationship.getEndNode();
				
				ParameterType parameterType = (ParameterType)ParameterTypeNEO.toBinding(parameterTypeNode);				
				binding.getParameter().add(parameterType);

			}
			
		}

		// - QUERY-EXPRESSION (0..1)
		relationships = node.getRelationships(RelationTypes.hasQueryExpression);
		if (relationships != null) {
		
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node queryExpresionTypeNode = relationship.getEndNode();

				try {

					Class<?> clazz = getClassNEO(queryExpresionTypeNode.getProperty(NEO4J_TYPE));
					Method method = clazz.getMethod("toBinding", Node.class);

					QueryExpressionType queryExpressionType = (QueryExpressionType)method.invoke(null, queryExpresionTypeNode);				
					binding.setQueryExpression(queryExpressionType);

				} catch (Exception e) {
					e.printStackTrace();
					
				}
				
			}
			
		}

		return binding;
		
	}
	
	public static String getNType() {
		return "QueryDefinitionType";
	}
}
