package de.kp.registry.server.neo4j.domain.classification;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ClassificationType;

import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class ClassificationTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new ClassificationType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		ClassificationType classificationType = (ClassificationType)binding;
		
		// - CLASSIFICATION NODE (0..1)
		String classificationNode = classificationType.getClassificationNode();
		
		// - CLASSIFIED-OBJECT (0..1)
		String classifiedObject = classificationType.getClassifiedObject();
		
		// - CLASSIFICATION SCHEME (0..1)
		String classificationScheme = classificationType.getClassificationScheme();
		
		// - NODE-REPRESENTATION (0..1)
		String nodeRepresentation = classificationType.getNodeRepresentation();

		// create node from underlying RegistryObjectType
		Node classificationTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);

		// - CLASSIFICATION NODE (0..1)
		if (classificationNode != null) classificationTypeNode.setProperty(OASIS_RIM_CLAS_NODE, classificationNode);

		// - CLASSIFIED-OBJECT (0..1)
		if (classifiedObject != null) classificationTypeNode.setProperty(OASIS_RIM_CLAS_OBJE, classifiedObject);

		// - CLASSIFICATION SCHEME (0..1)
		if (classificationScheme != null) classificationTypeNode.setProperty(OASIS_RIM_CLAS_SCHE, classificationScheme);

		// - NODE-REPRESENTATION (0..1)
		if (nodeRepresentation != null) classificationTypeNode.setProperty(OASIS_RIM_NODE_REPR, nodeRepresentation);

		return classificationTypeNode;
		
	}

	// this method replaces an existing ClassificationType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {		
		return null;
	}

	public static Object toBinding(Node node) {
		
		ClassificationType binding = factory.createClassificationType();
		binding = (ClassificationType)RegistryObjectTypeNEO.fillBinding(node, binding);

		// - CLASSIFICATION NODE (0..1)
		if (node.hasProperty(OASIS_RIM_CLAS_NODE)) binding.setClassificationNode((String)node.getProperty(OASIS_RIM_CLAS_NODE));

		// - CLASSIFIED-OBJECT (0..1)
		if (node.hasProperty(OASIS_RIM_CLAS_OBJE)) binding.setClassifiedObject((String)node.getProperty(OASIS_RIM_CLAS_OBJE));

		// - CLASSIFICATION SCHEME (0..1)
		if (node.hasProperty(OASIS_RIM_CLAS_SCHE)) binding.setClassificationScheme((String)node.getProperty(OASIS_RIM_CLAS_SCHE));

		// - NODE-REPRESENTATION (0..1)
		if (node.hasProperty(OASIS_RIM_NODE_REPR)) binding.setNodeRepresentation((String)node.getProperty(OASIS_RIM_NODE_REPR));

		return binding;
		
	}
	
	public static String getNType() {
		return "ClassificationType";
	}
}
