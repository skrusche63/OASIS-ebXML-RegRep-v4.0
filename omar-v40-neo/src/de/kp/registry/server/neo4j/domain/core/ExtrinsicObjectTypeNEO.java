package de.kp.registry.server.neo4j.domain.core;

import java.util.Iterator;

import javax.activation.DataHandler;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ExtrinsicObjectType;
import org.oasis.ebxml.registry.bindings.rim.SimpleLinkType;
import org.oasis.ebxml.registry.bindings.rim.VersionInfoType;

import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class ExtrinsicObjectTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new ExtrinsicObjectType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		ExtrinsicObjectType extrinsicObjectType = (ExtrinsicObjectType)binding;
		
		// - CONTENT VERSION INFO (0..1)
		VersionInfoType contentVersionInfo = extrinsicObjectType.getContentVersionInfo();
		
		// - MIMETYPE (0..1)
		String mimetype = extrinsicObjectType.getMimeType();
		
		// - REPOSITORY-ITEM (0..1)
		DataHandler repositoryItem = extrinsicObjectType.getRepositoryItem();
		
		// - REPOSITORY-ITEM-REF (0..1)
		SimpleLinkType repositoryItemRef = extrinsicObjectType.getRepositoryItemRef();

		// create node from underlying RegistryObjectType
		Node extrinsicObjectTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe an ExtrinsicObjectType
		extrinsicObjectTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - CONTENT VERSION INFO (0..1)
		if (contentVersionInfo != null) {

			Node versionInfoTypeNode = VersionInfoTypeNEO.toNode(graphDB, contentVersionInfo);
			extrinsicObjectTypeNode.createRelationshipTo(versionInfoTypeNode, RelationTypes.hasContentVersion);
			
		}

		// - MIMETYPE (0..1)
		if (mimetype != null) extrinsicObjectTypeNode.setProperty(OASIS_RIM_MIMETYPE, mimetype);

		// - REPOSITORY-ITEM (0..1)
		if (repositoryItem != null) {
			// TODO
		}

		// - REPOSITORY-ITEM-REF (0..1)
		if (repositoryItemRef != null) extrinsicObjectTypeNode.setProperty(OASIS_RIM_URI, repositoryItemRef.getHref());
		
		return extrinsicObjectTypeNode;

	}

	// this method replaces an existing ExtrinsicObjectType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return null;
	}

	public static Node clearNode(Node node) {
		
		// TODO
		return null;
		
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
