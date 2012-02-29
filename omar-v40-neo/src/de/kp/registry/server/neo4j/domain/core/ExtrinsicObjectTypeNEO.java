package de.kp.registry.server.neo4j.domain.core;

import javax.activation.DataHandler;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ExtrinsicObjectType;
import org.oasis.ebxml.registry.bindings.rim.SimpleLinkType;
import org.oasis.ebxml.registry.bindings.rim.VersionInfoType;

import de.kp.registry.server.neo4j.domain.RelationTypes;

public class ExtrinsicObjectTypeNEO extends RegistryObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
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
		Node extrinsicObjectTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding);
		
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

	public static Object toBinding(Node node) {
		
		ExtrinsicObjectType binding = factory.createExtrinsicObjectType();
		return binding;
		
	}
	
	public static Object fillBinding(Node node, Object binding) {
		return binding;
	}
	
	public static String getNType() {
		return "ExtrinsicObjectType";
	}
}
