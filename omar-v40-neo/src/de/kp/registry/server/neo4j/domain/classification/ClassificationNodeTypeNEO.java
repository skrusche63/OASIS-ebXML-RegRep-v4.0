package de.kp.registry.server.neo4j.domain.classification;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ClassificationNodeType;

import de.kp.registry.server.neo4j.domain.exception.RegistryException;


public class ClassificationNodeTypeNEO extends TaxonomyElementTypeNEO {

	// this method creates a new ClassificationNodeType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		ClassificationNodeType classificationNodeType = (ClassificationNodeType)binding;
		
		// - CODE (1..1)
		String code = classificationNodeType.getCode();
		
		// - PARENT (0..1)
		String parent = classificationNodeType.getParent();
		
		// - PATH (0..1)
		String path = classificationNodeType.getPath();
		
		// create node from underlying TaxonomyElementType
		Node classificationNodeTypeNode = TaxonomyElementTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a ClassificationNodeType
		classificationNodeTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - CODE (1..1)
		classificationNodeTypeNode.setProperty(OASIS_RIM_CODE, code);
		
		// - PARENT (0..1)
		if (parent != null) classificationNodeTypeNode.setProperty(OASIS_RIM_PARENT, parent);
		
		// - PATH (0..1)
		if (path != null) classificationNodeTypeNode.setProperty(OASIS_RIM_PATH, path);

		return classificationNodeTypeNode;
	
	}

	// this method replaces an existing ClassificationNodeType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {		
		return null;
	}

	public static Object toBinding(Node node) {
	
		ClassificationNodeType binding = factory.createClassificationNodeType();
		binding = (ClassificationNodeType)TaxonomyElementTypeNEO.fillBinding(node, binding);

		// - CODE (1..1)
		binding.setCode((String)node.getProperty(OASIS_RIM_CODE));

		// - PARENT (0..1)
		if (node.hasProperty(OASIS_RIM_PARENT)) binding.setParent((String)node.getProperty(OASIS_RIM_PARENT));

		// - PATH (0..1)
		if (node.hasProperty(OASIS_RIM_PATH)) binding.setPath((String)node.getProperty(OASIS_RIM_PATH));

		return binding;
		
	}
	
	public static String getNType() {
		return "ClassificationNodeType";
	}
	
}
