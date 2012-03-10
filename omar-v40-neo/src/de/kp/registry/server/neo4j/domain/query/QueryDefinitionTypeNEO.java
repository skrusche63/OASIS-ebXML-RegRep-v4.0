package de.kp.registry.server.neo4j.domain.query;

import java.lang.reflect.Method;
import java.util.ArrayList;
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
		return fillNode(graphDB, node, binding, checkReference, false);
	}

	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference, boolean excludeVersion) throws RegistryException {
		
		// clear QueryDefinitionType specific parameters
		node = clearNode(node, excludeVersion);
		
		// clear & fill node with RegistryObjectType specific parameters
		node = RegistryObjectTypeNEO.fillNode(graphDB, node, binding, checkReference, excludeVersion);
		
		// fill node with QueryDefinitionType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 

	}
	
	public static Node clearNode(Node node, boolean excludeVersion) {
		
		// - PARAMETER (0..*)

		// __DESIGN__
		
		// ParameterType nodes are an intrinsic part of a QueryDefinitionType
		// and are therefore removed in addition to the respective relationships
		
		// clear relationship and referenced ParameterType nodes (cascading removal)
		String deletionScope = "";
		
		boolean checkReference = false;
		boolean deleteChildren = false;
		
		node = clearParameters(node, checkReference, deleteChildren, deletionScope);
		
		// - QUERY-EXPRESSION (0..1)

		// QueryExpressionType node is an intrinsic part of a QueryDefinitionType
		// and are therefore removed in addition to the respective relationships
		
		// clear relationship and referenced QueryExpressionType node
		node = NEOBase.clearRelationship(node, RelationTypes.hasQueryExpression, true);

		return node;
		
	}

	// this is a common wrapper to delete a QueryDefinitionType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear QueryDefinitionType specific parameters
		node = clearNode(node, false);
		
		// clear node fromRegistryObjectType specific parameters and remove
		RegistryObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);

	}

	// __CASCADING REMOVAL__
	
	// this method is part of the cascading delete strategy for QueryDefinitionType nodes
	
	private static Node clearParameters(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		Iterable<Relationship> relationships = node.getRelationships(RelationTypes.hasParameter);
		if (relationships != null) {

			List<Object>removables = new ArrayList<Object>();

			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
				
				Relationship relationship = iterator.next();
				removables.add(relationship);
				
				Node endNode = relationship.getEndNode();
				removables.add(endNode);

			}

			// remove all collected node and relationships
			while (removables.size() > 0) {
				
				Object removable = removables.get(0);
				if (removable instanceof Node)
					// this is a dedicated removal of a ParameterType node
					ParameterTypeNEO.removeNode((Node)removable, checkReference, deleteChildren, deletionScope);
				
				else if (removable instanceof Relationship)
					((Relationship)removable).delete();
			}

		}

		return node;
		
	}
	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		// __DESIGN__
		
		// the parameter 'checkReference' must not be evaluated for QueryDefinitionType
		// as this node references ExtensibleObjectType nodes only; note, that this
		// nodes do not have a unique identifier
		
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
