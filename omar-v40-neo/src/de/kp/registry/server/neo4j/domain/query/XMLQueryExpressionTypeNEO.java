package de.kp.registry.server.neo4j.domain.query;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.XMLQueryExpressionType;

public class XMLQueryExpressionTypeNEO extends QueryExpressionTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {

		XMLQueryExpressionType xmlQueryExpressionType = (XMLQueryExpressionType)binding;

		// - OBJECT (1..1)
		Object value = xmlQueryExpressionType.getAny();
		
		// create node from underlying QueryExpressionType
		Node xmlQueryExpressionTypeNode = QueryExpressionTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a XMLQueryExpressionType
		xmlQueryExpressionTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - OBJECT (1..1)
		xmlQueryExpressionTypeNode.setProperty(OASIS_RIM_QUERY_VALUE, value);
		
		return xmlQueryExpressionTypeNode;

	}

	public static Object toBinding(Node node) {
		
		XMLQueryExpressionType binding = factory.createXMLQueryExpressionType();
		binding = (XMLQueryExpressionType)QueryExpressionTypeNEO.fillBinding(node, binding);

		// - OBJECT (1..1)
		binding.setAny(node.getProperty(OASIS_RIM_QUERY_VALUE));
		
		return binding;
		
	}

	public static String getNType() {
		return "XMLQueryExpressionType";
	}
}
