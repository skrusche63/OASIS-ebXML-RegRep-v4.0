package de.kp.registry.server.neo4j.domain.core;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ExternalIdentifierType;

import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.exception.UnresolvedReferenceException;
import de.kp.registry.server.neo4j.read.ReadManager;


public class ExternalIdentifierTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new ExternalIdentifierType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {

		// create node from underlying RegistryObjectType
		Node node = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a ExternalIdentifierType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);
	
	}

	// this method replaces an existing ExternalIdentifierType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return fillNode(graphDB, node, binding, checkReference, false);
	}
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference, boolean excludeVersion) throws RegistryException {

		// clear ExternalIdentifierType specific parameters
		node = clearNode(node, excludeVersion);

		// clear & fill node with RegistryObjectType specific parameters
		node = RegistryObjectTypeNEO.fillNode(graphDB, node, binding, checkReference, excludeVersion);
		
		// fill node with ExternalIdentifierType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 
	}

	public static Node clearNode(Node node, boolean excludeVersion) {
		
		// - IDENTIFICATION SCHEME (1..1)
		node.removeProperty(OASIS_RIM_ID_SCHEME);
		
		// - REGISTRY-OBJECT (0..1)
		if (node.hasProperty(OASIS_RIM_PARENT)) node.removeProperty(OASIS_RIM_PARENT);

		// - VALUE (1..1)
		node.removeProperty(OASIS_RIM_VALUE);
		return node;
		
	}
	
	// this is a common wrapper to delete ExternalIdentifierType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear ExternalIdentifierType specific parameters
		node = clearNode(node, false);
		
		// clear node from RegistryObjectType specific parameters and remove
		RegistryObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}
	
	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		ExternalIdentifierType externalIdentifierType = (ExternalIdentifierType)binding;
		
		// - IDENTIFICATION SCHEME (1..1)
		String identificationScheme = externalIdentifierType.getIdentificationScheme();
		
		// - REGISTRY-OBJECT (0..1)
		String parent = externalIdentifierType.getRegistryObject();
		
		// - VALUE (1..1)
		String value = externalIdentifierType.getValue();
		
		// ===== FILL NODE =====

		// - IDENTIFICATION SCHEME (1..1)
		if (checkReference == true) {
			// make sure that the Identification Scheme references an existing node within the database
			if (ReadManager.getInstance().findNodeByID(parent) == null) 
				throw new UnresolvedReferenceException("[ExternalIdentifierType] Identification Scheme node with id '" + identificationScheme + "' does not exist.");		

		}

		node.setProperty(OASIS_RIM_ID_SCHEME, identificationScheme);
		
		// - REGISTRY-OBJECT (0..1)
		if (parent != null) {
			
			
			if (checkReference == true) {
				// make sure that the parent references an existing node within the database
				if (ReadManager.getInstance().findNodeByID(parent) == null) 
					throw new UnresolvedReferenceException("[ExternalIdentifierType] Parent node with id '" + parent + "' does not exist.");		

			}

			node.setProperty(OASIS_RIM_PARENT, parent);
		}

		// - VALUE (1..1)
		node.setProperty(OASIS_RIM_VALUE, value);
		
		return node;
		
	}

	public static Object toBinding(Node node) {
		return toBinding(node, null);
	}

	public static Object toBinding(Node node, String language) {
		
		ExternalIdentifierType binding = factory.createExternalIdentifierType();
		binding = (ExternalIdentifierType)RegistryObjectTypeNEO.fillBinding(node, binding, language);

		// - IDENTIFICATION SCHEME (1..1)
		binding.setIdentificationScheme((String)node.getProperty(OASIS_RIM_ID_SCHEME));
		
		// - REGISTRY-OBJECT (0..1)
		if (node.hasProperty(OASIS_RIM_PARENT)) binding.setRegistryObject((String)node.getProperty(OASIS_RIM_PARENT));

		// - VALUE (1..1)
		binding.setValue((String)node.getProperty(OASIS_RIM_VALUE));
		
		return binding;
		
	}
	
	public static String getNType() {
		return "ExternalIdentifierType";
	}
	
}
