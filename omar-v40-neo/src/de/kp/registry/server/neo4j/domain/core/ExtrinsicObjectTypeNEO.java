package de.kp.registry.server.neo4j.domain.core;

import java.util.Iterator;

import javax.activation.DataHandler;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ExtrinsicObjectType;
import org.oasis.ebxml.registry.bindings.rim.SimpleLinkType;
import org.oasis.ebxml.registry.bindings.rim.VersionInfoType;

import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class ExtrinsicObjectTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new ExtrinsicObjectType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {

		// create node from underlying RegistryObjectType
		Node node = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe an ExtrinsicObjectType
		node.setProperty(NEO4J_TYPE, getNType());
		
		return fillNodeInternal(graphDB, node, binding, checkReference);

	}

	// this method replaces an existing ExtrinsicObjectType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return fillNode(graphDB, node, binding, checkReference, false);
	}
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference, boolean excludeVersion) throws RegistryException {

		// clear ExtrinsicObjectType specific parameters
		node = clearNode(node, excludeVersion);

		// clear & fill node with RegistryObjectType specific parameters
		node = RegistryObjectTypeNEO.fillNode(graphDB, node, binding, checkReference, excludeVersion);
		
		// fill node with ExtrinsicObjectType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 

	}

	public static Node clearNode(Node node, boolean excludeVersion) {
		
		// - CONTENT VERSION INFO (0..1)

		// clear relationship and referenced VersionInfoType nodes
		node = NEOBase.clearRelationship(node, RelationTypes.hasClassification, true);

		// - MIMETYPE (0..1)
		if (node.hasProperty(OASIS_RIM_MIMETYPE)) node.removeProperty(OASIS_RIM_MIMETYPE);
		
		// - REPOSITORY-ITEM (0..1)
		clearRepositoryItem(node);
		
		// - REPOSITORY-ITEM-REF (0..1)
		if (node.hasProperty(OASIS_RIM_URI)) node.removeProperty(OASIS_RIM_URI);
		
		return node;
		
	}

	// this is a common wrapper to delete ExtrinsicObjectType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear ExtrinsicObjectType specific parameters
		node = clearNode(node, false);
		
		// clear node from RegistryObjectType specific parameters and remove
		RegistryObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}

	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		// parameter 'checkReference' must not be evaluated for ExtrinsicObjectType node
		
		ExtrinsicObjectType extrinsicObjectType = (ExtrinsicObjectType)binding;
		
		// - CONTENT VERSION INFO (0..1)
		VersionInfoType contentVersionInfo = extrinsicObjectType.getContentVersionInfo();
		
		// - MIMETYPE (0..1)
		String mimetype = extrinsicObjectType.getMimeType();
		
		// - REPOSITORY-ITEM (0..1)
		DataHandler repositoryItem = extrinsicObjectType.getRepositoryItem();
		
		// - REPOSITORY-ITEM-REF (0..1)
		SimpleLinkType repositoryItemRef = extrinsicObjectType.getRepositoryItemRef();
		
		// ===== FILL NODE =====

		// - CONTENT VERSION INFO (0..1)
		if (contentVersionInfo != null) {

			Node versionInfoTypeNode = VersionInfoTypeNEO.toNode(graphDB, contentVersionInfo);
			node.createRelationshipTo(versionInfoTypeNode, RelationTypes.hasContentVersion);
			
		}

		// - MIMETYPE (0..1)
		if (mimetype != null) node.setProperty(OASIS_RIM_MIMETYPE, mimetype);

		// - REPOSITORY-ITEM (0..1)
		if (repositoryItem != null) setRepositoryItem(node, repositoryItem);

		// - REPOSITORY-ITEM-REF (0..1)
		if (repositoryItemRef != null) node.setProperty(OASIS_RIM_URI, repositoryItemRef.getHref());
		
		return node;

	}

	// this is a helper method to assign a repository item to the
	// respective ExtrinsicObjectType node
	
	private static void setRepositoryItem(Node node, DataHandler repositoryItem) {
		// TODO
	}
	
	// this is a helper method to clear a certain repository item
	// from the respective ExtrinsicObjectTpye node and also delete
	// the associated file
	
	private static void clearRepositoryItem(Node node) {
		// TODO
	}
	
	public static Object toBinding(Node node) {
		return fillBinding(node, factory.createExtrinsicObjectType());
	}
	
	public static Object fillBinding(Node node, Object binding) {
		
		ExtrinsicObjectType extrinsicObjectType = (ExtrinsicObjectType)RegistryObjectTypeNEO.fillBinding(node, binding);

		// - CONTENT VERSION INFO (0..1)
		Iterable<Relationship> relationships = null;
		
		relationships = node.getRelationships(RelationTypes.hasContentVersion);
		if (relationships != null) {
		
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node versionInfoTypeNode = relationship.getEndNode();
				
				VersionInfoType versionInfoType = (VersionInfoType)VersionInfoTypeNEO.toBinding(versionInfoTypeNode);				
				extrinsicObjectType.setContentVersionInfo(versionInfoType);

			}
			
		}

		// - MIMETYPE (0..1)
		if (node.hasProperty(OASIS_RIM_MIMETYPE)) extrinsicObjectType.setMimeType((String)node.getProperty(OASIS_RIM_MIMETYPE));
		
		// - REPOSITORY-ITEM (0..1)
		
		// - REPOSITORY-ITEM-REF (0..1)
		if (node.hasProperty(OASIS_RIM_URI)) {
			
			SimpleLinkType simpleLinkType = factory.createSimpleLinkType();
			simpleLinkType.setHref((String)node.getProperty(OASIS_RIM_URI));
			
			extrinsicObjectType.setRepositoryItemRef(simpleLinkType);

		}
		
		return extrinsicObjectType;
		
	}
	
	public static String getNType() {
		return "ExtrinsicObjectType";
	}
}
