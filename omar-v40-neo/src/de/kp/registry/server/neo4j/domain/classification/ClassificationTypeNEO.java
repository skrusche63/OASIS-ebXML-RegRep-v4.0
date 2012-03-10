package de.kp.registry.server.neo4j.domain.classification;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ClassificationType;

import de.kp.registry.server.neo4j.database.ReadManager;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.exception.UnresolvedReferenceException;

public class ClassificationTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new ClassificationType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {

		// create node from underlying RegistryObjectType
		Node node = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);

		// update the internal type to describe a ClassificationType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);
		
	}

	// this method replaces an existing ClassificationType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier

	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return fillNode(graphDB, node, binding, checkReference, false);
	}
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference, boolean excludeVersion) throws RegistryException {		

		// clear ClassificationType specific parameters
		node = clearNode(node, excludeVersion);

		// clear & fill node with RegistryObjectType specific parameters
		node = RegistryObjectTypeNEO.fillNode(graphDB, node, binding, checkReference, excludeVersion);
		
		// fill node with ClassificationType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 
	
	}

	public static Node clearNode(Node node, boolean excludeVersion) {

		// - CLASSIFICATION NODE (0..1)
		if (node.hasProperty(OASIS_RIM_CLAS_NODE)) node.removeProperty(OASIS_RIM_CLAS_NODE);

		// - CLASSIFIED-OBJECT (0..1)
		if (node.hasProperty(OASIS_RIM_CLAS_OBJE)) node.removeProperty(OASIS_RIM_CLAS_OBJE);

		// - CLASSIFICATION SCHEME (0..1)
		if (node.hasProperty(OASIS_RIM_CLAS_SCHE)) node.removeProperty(OASIS_RIM_CLAS_SCHE);

		// - NODE-REPRESENTATION (0..1)
		if (node.hasProperty(OASIS_RIM_NODE_REPR)) node.removeProperty(OASIS_RIM_NODE_REPR);

		return node;
		
	}

	// this is a common wrapper to delete ClassificationType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear ClassificationType specific parameters
		node = clearNode(node, false);
		
		// clear node from RegistryObjectType specific parameters and remove
		RegistryObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}
	
	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		ClassificationType classificationType = (ClassificationType)binding;
		
		// - CLASSIFICATION NODE (0..1)
		String classificationNode = classificationType.getClassificationNode();
		
		// - CLASSIFIED-OBJECT (0..1)
		String classifiedObject = classificationType.getClassifiedObject();
		
		// - CLASSIFICATION SCHEME (0..1)
		String classificationScheme = classificationType.getClassificationScheme();
		
		// - NODE-REPRESENTATION (0..1)
		String nodeRepresentation = classificationType.getNodeRepresentation();
		
		// ===== FILL NODE =====

		// - CLASSIFICATION NODE (0..1)
		if (classificationNode != null) {

			if (checkReference == true) {
				// make sure that the ClassificationNode references an existing node within the database
				if (ReadManager.getInstance().findNodeByID(classificationNode) == null) 
					throw new UnresolvedReferenceException("[ClassificationType] Classification node with id '" + classificationNode + "' does not exist.");		

			}
			
			node.setProperty(OASIS_RIM_CLAS_NODE, classificationNode);
		}

		// - CLASSIFIED-OBJECT (0..1)
		if (classifiedObject != null) {

			if (checkReference == true) {
				// make sure that the classified object references an existing node within the database
				if (ReadManager.getInstance().findNodeByID(classifiedObject) == null) 
					throw new UnresolvedReferenceException("[ClassificationType] Classified object with id '" + classifiedObject + "' does not exist.");		

			}

			node.setProperty(OASIS_RIM_CLAS_OBJE, classifiedObject);
		}

		// - CLASSIFICATION SCHEME (0..1)
		if (classificationScheme != null) {
			
			if (checkReference == true) {
				// make sure that the ClassificationScheme references an existing node within the database
				if (ReadManager.getInstance().findNodeByID(classificationScheme) == null) 
					throw new UnresolvedReferenceException("[ClassificationType] Classification Scheme with id '" + classificationScheme + "' does not exist.");		

			}
			
			node.setProperty(OASIS_RIM_CLAS_SCHE, classificationScheme);
		}

		// - NODE-REPRESENTATION (0..1)
		if (nodeRepresentation != null) node.setProperty(OASIS_RIM_NODE_REPR, nodeRepresentation);

		return node;

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
