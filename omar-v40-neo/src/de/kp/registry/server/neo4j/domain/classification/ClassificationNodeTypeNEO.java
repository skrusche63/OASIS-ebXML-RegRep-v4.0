package de.kp.registry.server.neo4j.domain.classification;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ClassificationNodeType;

import de.kp.registry.server.neo4j.database.ReadManager;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.exception.UnresolvedReferenceException;


public class ClassificationNodeTypeNEO extends TaxonomyElementTypeNEO {

	// this method creates a new ClassificationNodeType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {

		// create node from underlying TaxonomyElementType
		Node node = TaxonomyElementTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a ClassificationNodeType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);
	
	}

	// this method replaces an existing ClassificationNodeType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier

	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return fillNode(graphDB, node, binding, checkReference, false);
	}

	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference, boolean excludeVersion) throws RegistryException {		

		// clear ClassificationNodeType specific parameters
		node = clearNode(node, excludeVersion);

		// clear & fill node with TaxonomyElementType specific parameters
		node = TaxonomyElementTypeNEO.fillNode(graphDB, node, binding, checkReference, excludeVersion);
		
		// fill node with ClassificationNodeType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 
	}

	public static Node clearNode(Node node, boolean excludeVersion) {

		// - CODE (1..1)
		node.removeProperty(OASIS_RIM_CODE);
		
		// - PARENT (0..1)
		if (node.hasProperty(OASIS_RIM_PARENT)) node.removeProperty(OASIS_RIM_PARENT);
		
		// - PATH (0..1)
		if (node.hasProperty(OASIS_RIM_PATH)) node.removeProperty(OASIS_RIM_PATH);

		return node;
		
	}
	
	// this is a common wrapper to delete ClassificationNodeType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear ClassificationNodeType specific parameters
		node = clearNode(node, false);
		
		// clear node from TaxonomyElementType specific parameters and remove
		TaxonomyElementTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}
	
	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		ClassificationNodeType classificationNodeType = (ClassificationNodeType)binding;
		
		// - CODE (1..1)
		String code = classificationNodeType.getCode();
		
		// - PARENT (0..1)
		String parent = classificationNodeType.getParent();
		
		// - PATH (0..1)
		String path = classificationNodeType.getPath();
				
		// ===== FILL NODE =====

		// - CODE (1..1)
		node.setProperty(OASIS_RIM_CODE, code);
		
		// - PARENT (0..1)
		if (parent != null) {

			if (checkReference == true) {
				// make sure that the parent references an existing node within the database
				if (ReadManager.getInstance().findNodeByID(parent) == null) 
					throw new UnresolvedReferenceException("[ClassificationNodeType] Parent node with id '" + parent + "' does not exist.");		

			}

			node.setProperty(OASIS_RIM_PARENT, parent);
		}
		
		// - PATH (0..1)
		if (path != null) node.setProperty(OASIS_RIM_PATH, path);

		return node;

	}

	public static Object toBinding(Node node) {
		return toBinding(node, null);
	}

	public static Object toBinding(Node node, String language) {
	
		ClassificationNodeType binding = factory.createClassificationNodeType();
		binding = (ClassificationNodeType)TaxonomyElementTypeNEO.fillBinding(node, binding, language);

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
