package de.kp.registry.server.neo4j.domain.classification;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ClassificationSchemeType;

public class ClassificationSchemeTypeNEO extends TaxonomyElementTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		ClassificationSchemeType classificationSchemeType = (ClassificationSchemeType)binding;
		
		// - IS-INTERNAL (1..1)
		Boolean isInternal = classificationSchemeType.isIsInternal();
		
		// - NODE-TYPE (1..1)
		String nodeType = classificationSchemeType.getNodeType();
		
		// create node from underlying TaxonomyElementType
		Node classificationSchemeTypeNode = TaxonomyElementTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a ClassificationSchemeType
		classificationSchemeTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - IS-INTERNAL (1..1)
		classificationSchemeTypeNode.setProperty(OASIS_RIM_IS_INTERNAL, isInternal);
		
		// - NODE-TYPE (1..1)
		classificationSchemeTypeNode.setProperty(OASIS_RIM_NODE_TYPE, nodeType);

		return classificationSchemeTypeNode;
	}

	public static Object toBinding(Node node) {
		
		ClassificationSchemeType binding = factory.createClassificationSchemeType();
		binding = (ClassificationSchemeType)TaxonomyElementTypeNEO.fillBinding(node, binding);

		// - IS-INTERNAL (1..1)
		Boolean isInternal = (Boolean)node.getProperty(OASIS_RIM_IS_INTERNAL);
		binding.setIsInternal(isInternal);

		// - NODE-TYPE (1..1)
		String nodeType = (String)node.getProperty(OASIS_RIM_NODE_TYPE);
		binding.setNodeType(nodeType);

		return binding;
		
	}

	public static String getNType() {
		return "ClassificationSchemeType";
	}
}
