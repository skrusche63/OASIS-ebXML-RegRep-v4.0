package de.kp.registry.server.neo4j.domain.association;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.AssociationType;

import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.exception.UnresolvedReferenceException;
import de.kp.registry.server.neo4j.read.ReadManager;

public class AssociationTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new AssociationType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		// create node from underlying RegistryObjectType
		Node node = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe an AssociationType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);

	}

	// this method replaces an existing AssociationType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier

	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return fillNode(graphDB, node, binding, checkReference, false);
	}
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference, boolean excludeVersion) throws RegistryException {		

		// clear AssociationType specific parameters
		node = clearNode(node, false);
		
		// clear & fill node with RegistryObjectType specific parameters
		node = RegistryObjectTypeNEO.fillNode(graphDB, node, binding, checkReference, excludeVersion);
		
		// fill node with AssociationType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 
	
	}

	public static Node clearNode(Node node, boolean excludeVersion) {

		// - SOURCE-OBJECT (1..1)
		node.removeProperty(OASIS_RIM_SOURCE);
		
		// - TARGET-OBJECT (1..1)
		node.removeProperty(OASIS_RIM_TARGET);

		// - TYPE (1..1)
		node.removeProperty(OASIS_RIM_TYPE);

		return node;
		
	}

	// this is a common wrapper to delete AssociationType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear AssociationType specific parameters
		node = clearNode(node, false);
		
		// clear node from RegistryObjectType specific parameters and remove
		RegistryObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}

	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		AssociationType associationType = (AssociationType)binding;
		
		// - SOURCE-OBJECT (1..1)
		String sourceObject = associationType.getSourceObject();
		
		// - TARGET-OBJECT (1..1)
		String targetObject = associationType.getTargetObject();
		
		// - TYPE (1..1)
		String type = associationType.getType();

		// ===== FILL NODE =====

		if (checkReference == true) {
			// make sure that the sourceObject references an existing node within the database
			if (ReadManager.getInstance().findNodeByID(sourceObject) == null) throw new UnresolvedReferenceException("[AssociationType] Source object with id '" + sourceObject + "' does not exist.");		

			// make sure that the targetObject references an existing node within the database
			if (ReadManager.getInstance().findNodeByID(targetObject) == null) throw new UnresolvedReferenceException("[AssociationType] Target object with id '" + targetObject + "' does not exist.");		

		}

		// - SOURCE-OBJECT (1..1)
		node.setProperty(OASIS_RIM_SOURCE, sourceObject);
		
		// - TARGET-OBJECT (1..1)
		node.setProperty(OASIS_RIM_TARGET, targetObject);

		// - TYPE (1..1)
		node.setProperty(OASIS_RIM_TYPE, type);
		
		return node;

	}

	public static Object toBinding(Node node) {
		return toBinding(node, null);
	}

	public static Object toBinding(Node node, String language) {
	
		AssociationType binding = factory.createAssociationType();
		binding = (AssociationType)RegistryObjectTypeNEO.fillBinding(node, binding, language);

		// - SOURCE-OBJECT (1..1)
		binding.setSourceObject((String)node.getProperty(OASIS_RIM_SOURCE));
		
		// - TARGET-OBJECT (1..1)
		binding.setTargetObject((String)node.getProperty(OASIS_RIM_TARGET));
		
		// - TYPE (1..1)
		binding.setType((String)node.getProperty(OASIS_RIM_TYPE));
		
		return binding;		
		
	}
	
	public static String getNType() {
		return "AssociationType";
	}
}
