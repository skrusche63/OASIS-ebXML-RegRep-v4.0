package de.kp.registry.server.neo4j.domain.core;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ExternalLinkType;
import org.oasis.ebxml.registry.bindings.rim.SimpleLinkType;

import de.kp.registry.server.neo4j.database.ReadManager;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.exception.UnresolvedReferenceException;


public class ExternalLinkTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new ExternalLinkType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {

		// create node from underlying RegistryObjectType
		Node node = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a ExternalLinkType
		node.setProperty(NEO4J_TYPE, getNType());
		
		return fillNodeInternal(graphDB, node, binding, checkReference);
		
	}

	// this method replaces an existing ExternalLinkType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return fillNode(graphDB, node, binding, checkReference, false);
	}
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference, boolean excludeVersion) throws RegistryException {

		// clear ExternalLinkType specific parameters
		node = clearNode(node, excludeVersion);

		// clear & fill node with RegistryObjectType specific parameters
		node = RegistryObjectTypeNEO.fillNode(graphDB, node, binding, checkReference, excludeVersion);
		
		// fill node with ExternalLinkType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 

	}

	public static Node clearNode(Node node, boolean excludeVersion) {
		
		// - EXTERNAL-REF (1..1)
		node.removeProperty(OASIS_RIM_URI);
		
		// - REGISTRY-OBJECT (0..1)
		if (node.hasProperty(OASIS_RIM_PARENT)) node.removeProperty(OASIS_RIM_PARENT);

		return node;
		
	}

	// this is a common wrapper to delete ExternalLinkType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear ExternalLinkType specific parameters
		node = clearNode(node, false);
		
		// clear node from RegistryObjectType specific parameters and remove
		RegistryObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}
	
	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		ExternalLinkType externalLinkType = (ExternalLinkType)binding;
		
		// - EXTERNAL-REF (1..1)
		SimpleLinkType externalRef = externalLinkType.getExternalRef();

		// the externalRef parameter is actually restricted to a URI
		String externalURI = externalRef.getHref();
		
		// - REGISTRY-OBJECT (0..1)
		String parent = externalLinkType.getRegistryObject();
		
		// ===== FILL NODE =====

		// - EXTERNAL-REF (1..1)
		node.setProperty(OASIS_RIM_URI, externalURI);
		
		// - REGISTRY-OBJECT (0..1)
		if (parent != null) {
			
			if (checkReference == true) {
				// make sure that the parent references an existing node within the database
				if (ReadManager.getInstance().findNodeByID(parent) == null) 
					throw new UnresolvedReferenceException("[ExternalLinkType] Parent node with id '" + parent + "' does not exist.");		

			}

			node.setProperty(OASIS_RIM_PARENT, parent);
		
		}

		return node;

	}

	public static Object toBinding(Node node) {

		ExternalLinkType binding = factory.createExternalLinkType();
		binding = (ExternalLinkType)RegistryObjectTypeNEO.fillBinding(node, binding);

		// - EXTERNAL-REF (1..1)
		if (node.hasProperty(OASIS_RIM_URI)) {
		
			SimpleLinkType simpleLinkType = factory.createSimpleLinkType();
			simpleLinkType.setHref((String)node.getProperty(OASIS_RIM_URI));
			
			binding.setExternalRef(simpleLinkType);
		
		}

		// - REGISTRY-OBJECT (0..1)
		if (node.hasProperty(OASIS_RIM_PARENT)) binding.setRegistryObject((String)node.getProperty(OASIS_RIM_PARENT));
		
		return binding;
		
	}
	
	public static String getNType() {
		return "ExternalLinkType";
	}
	
}
