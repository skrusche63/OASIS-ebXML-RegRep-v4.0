package de.kp.registry.server.neo4j.domain.classification;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ClassificationSchemeType;

import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class ClassificationSchemeTypeNEO extends TaxonomyElementTypeNEO {

	// this method creates a new ClassificationSchemeType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		// create node from underlying TaxonomyElementType
		Node node = TaxonomyElementTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a ClassificationSchemeType
		node.setProperty(NEO4J_TYPE, getNType());
				
		// fill node with ClassificationSchemeType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 

	}

	// this method replaces an existing ClassificationSchemeType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier

	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return fillNode(graphDB, node, binding, checkReference, false);
	}

	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference, boolean excludeVersion) throws RegistryException {		

		// clear ClassificationSchemeType specific parameters
		node = clearNode(node, excludeVersion);

		// clear & fill node with TaxonomyElementType specific parameters
		node = TaxonomyElementTypeNEO.fillNode(graphDB, node, binding, checkReference, excludeVersion);
		
		// fill node with ClassificationSchemeType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 

	}

	public static Node clearNode(Node node, boolean excludeVersion) {

		// - IS-INTERNAL (1..1)
		node.removeProperty(OASIS_RIM_IS_INTERNAL);

		// - NODE-TYPE (1..1)
		node.removeProperty(OASIS_RIM_NODE_TYPE);
		
		return node;
		
	}

	// this is a common wrapper to delete ClassificationSchemeType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear ClassificationSchemeType specific parameters
		node = clearNode(node, false);
		
		// clear node from TaxonomyElementType specific parameters and remove
		TaxonomyElementTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}
	
	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		// __DESIGN__
		
		// the parameter 'checkReference' must not be evaluated for ClassificationSchemeType
		
		ClassificationSchemeType classificationSchemeType = (ClassificationSchemeType)binding;
		
		// - IS-INTERNAL (1..1)
		Boolean isInternal = classificationSchemeType.isIsInternal();
		
		// - NODE-TYPE (1..1)
		String nodeType = classificationSchemeType.getNodeType();
		
		// ===== FILL NODE =====

		// - IS-INTERNAL (1..1)
		node.setProperty(OASIS_RIM_IS_INTERNAL, isInternal);
		
		// - NODE-TYPE (1..1)
		node.setProperty(OASIS_RIM_NODE_TYPE, nodeType);

		return node;

	}

	public static Object toBinding(Node node) {
		return toBinding(node, null);
	}

	public static Object toBinding(Node node, String language) {
		
		ClassificationSchemeType binding = factory.createClassificationSchemeType();
		binding = (ClassificationSchemeType)TaxonomyElementTypeNEO.fillBinding(node, binding, language);

		// - IS-INTERNAL (1..1)
		binding.setIsInternal((Boolean)node.getProperty(OASIS_RIM_IS_INTERNAL));

		// - NODE-TYPE (1..1)
		binding.setNodeType((String)node.getProperty(OASIS_RIM_NODE_TYPE));

		return binding;
		
	}

	public static String getNType() {
		return "ClassificationSchemeType";
	}
}
