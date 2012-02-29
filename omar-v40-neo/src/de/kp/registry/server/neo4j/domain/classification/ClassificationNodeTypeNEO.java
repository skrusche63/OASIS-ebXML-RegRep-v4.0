package de.kp.registry.server.neo4j.domain.classification;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ClassificationNodeType;


public class ClassificationNodeTypeNEO extends TaxonomyElementTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		ClassificationNodeType classificationNodeType = (ClassificationNodeType)binding;
		
		// - CODE (1..1)
		String code = classificationNodeType.getCode();
		
		// - PARENT (0..1)
		String parent = classificationNodeType.getParent();
		
		// - PATH (0..1)
		String path = classificationNodeType.getPath();
		
		// create node from underlying TaxonomyElementType
		Node classificationNodeTypeNode = TaxonomyElementTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a ClassificationNodeType
		classificationNodeTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - CODE (1..1)
		classificationNodeTypeNode.setProperty(OASIS_RIM_CODE, code);
		
		// - PARENT (0..1)
		if (parent != null) classificationNodeTypeNode.setProperty(OASIS_RIM_PARENT, code);
		
		// - PATH (0..1)
		if (path != null) classificationNodeTypeNode.setProperty(OASIS_RIM_PATH, code);

		return classificationNodeTypeNode;
	
	}

	public static Object toBinding(Node node) {
	
		ClassificationNodeType binding = factory.createClassificationNodeType();
		binding = (ClassificationNodeType)TaxonomyElementTypeNEO.fillBinding(node, binding);

		// - CODE (1..1)
		String code = (String)node.getProperty(OASIS_RIM_CODE);
		binding.setCode(code);

		// - PARENT (0..1)
		String parent = (String)node.getProperty(OASIS_RIM_PARENT);
		if (parent != null) binding.setParent(parent);

		// - PATH (0..1)
		String path = (String)node.getProperty(OASIS_RIM_PATH);
		if (path != null) binding.setPath(path);

		return binding;
		
	}
	
	public static String getNType() {
		return "ClassificationNodeType";
	}
	
}
