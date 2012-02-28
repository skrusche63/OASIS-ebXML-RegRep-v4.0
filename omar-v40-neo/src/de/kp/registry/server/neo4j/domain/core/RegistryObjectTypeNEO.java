package de.kp.registry.server.neo4j.domain.core;

import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ClassificationType;
import org.oasis.ebxml.registry.bindings.rim.ExternalIdentifierType;
import org.oasis.ebxml.registry.bindings.rim.ExternalLinkType;
import org.oasis.ebxml.registry.bindings.rim.InternationalStringType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;
import org.oasis.ebxml.registry.bindings.rim.VersionInfoType;

import de.kp.registry.server.neo4j.domain.ExternalIdentifierTypeNEO;
import de.kp.registry.server.neo4j.domain.ExternalLinkTypeNEO;
import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.classification.ClassificationTypeNEO;

/*
 * This class is introduced to fill a node with RegistryObjectType
 * specific properties and relations to other nodes
 * 
 * The RegistryObjectType is supported as an externally used object
 * and must provide a create-or-get mechanism 
 */

public class RegistryObjectTypeNEO extends IdentifiableTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		RegistryObjectType registryObjectType = (RegistryObjectType)binding;

		// - CLASSIFICATION (0..*)
		List<ClassificationType> classifications = registryObjectType.getClassification();
		
		// - DESCRIPTION (0..1)
		InternationalStringType registryObjectTypeDescription = registryObjectType.getDescription();
		
		// - EXTERNAL-IDENTIFIER (0..*)
		List<ExternalIdentifierType> externalIdentifiers = registryObjectType.getExternalIdentifier();
		
		// - EXTERNAL-LINK (0..*)
		List<ExternalLinkType> externalLinks = registryObjectType.getExternalLink();
		
		// - LID (0..1)
		String registryObjectTypeLid = registryObjectType.getLid();
		
		// - NAME (0..1)
		InternationalStringType registryObjectTypeName = registryObjectType.getName();
		
		// - OBJECT-TYPE (0..1)
		String registryObjectTypeType = registryObjectType.getObjectType();
		
		// - OWNER (0..1)
		String registryObjectTypeOwner = registryObjectType.getOwner();
		
		// - STATUS (0..1)
		String registryObjectTypeStatus = registryObjectType.getStatus();
		
		// - VERSION-INFO (0..1)
		VersionInfoType versionInfo = registryObjectType.getVersionInfo();
				
		// TODO: here we have to determine whether we retrieve the respective node
		// from the database (if it exists) or through creation
		
		
		// create node from underlying IdentifiableType
		Node registryObjectTypeNode = IdentifiableTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a RegistryObjectType
		registryObjectTypeNode.setProperty(NEO4J_TYPE, getNType());
				
		// - CLASSIFICATION (0..*)
		if (classifications.isEmpty() == false) {
			
			for (ClassificationType classification:classifications) {
				
				// a ClassificationType is always created when the respective
				// RegistryObjectType is created, i.e. the ClassificationType
				// is composed within the RegistryObjectType
				
				Node classificationTypeNode = ClassificationTypeNEO.toNode(graphDB, classification);
				registryObjectTypeNode.createRelationshipTo(classificationTypeNode, RelationTypes.hasClassification);
				
			}
		
		}
		
		// - DESCRIPTION (0..1)
		if (registryObjectTypeDescription != null) {
			
			Node internationalStringTypeNode = InternationalStringTypeNEO.toNode(graphDB, registryObjectTypeDescription);
			registryObjectTypeNode.createRelationshipTo(internationalStringTypeNode, RelationTypes.hasDescription);

		}
		
		// - EXTERNAL-IDENTIFIER (0..*)
		if (externalIdentifiers.isEmpty() == false) {
		
			for (ExternalIdentifierType externalIdentifier:externalIdentifiers) {
				
				// an ExternalIdentifierType is always created when the respective
				// RegistryObjectType is created, i.e. the ExternalIdentifierType
				// is composed within the RegistryObjectType
				
				// TODO: reference to registry object (=parent)

				Node externalIdentifierTypeNode = ExternalIdentifierTypeNEO.toNode(graphDB, externalIdentifier);
				registryObjectTypeNode.createRelationshipTo(externalIdentifierTypeNode, RelationTypes.hasIdentifier);
			
			}
			
		}
		
		// - EXTERNAL-LINK (0..*)
		if (externalLinks.isEmpty()) {
			
			for (ExternalLinkType externalLink:externalLinks) {
			
				// an ExternalLinkType may already exist when the respective
				// RegistryObjectType is created, i.e. the ExternalLinkType
				// is composed within the RegistryObjectType
				
				// TODO: reference to registry object (=parent)
				Node externalLinkTypeNode = ExternalLinkTypeNEO.toNode(graphDB, externalLink);
				registryObjectTypeNode.createRelationshipTo(externalLinkTypeNode, RelationTypes.hasLink);
				
			}
			
		}
		
		// - LID (0..1)
		if (registryObjectTypeLid != null) registryObjectTypeNode.setProperty(OASIS_RIM_LID, registryObjectTypeLid);
		
		// - NAME (0..1)
		if (registryObjectTypeName != null) {
			
			Node internationalStringTypeNode = InternationalStringTypeNEO.toNode(graphDB, registryObjectTypeName);
			registryObjectTypeNode.createRelationshipTo(internationalStringTypeNode, RelationTypes.hasName);

		}
		
		// - OBJECT-TYPE (0..1)
		if (registryObjectTypeType != null) registryObjectTypeNode.setProperty(OASIS_RIM_TYPE, registryObjectTypeType);
		
		// - OWNER (0..1)
		if (registryObjectTypeOwner != null) registryObjectTypeNode.setProperty(OASIS_RIM_OWNER, registryObjectTypeOwner);
		
		// - STATUS (0..1)
		if (registryObjectTypeStatus != null) registryObjectTypeNode.setProperty(OASIS_RIM_STATUS, registryObjectTypeStatus);
		
		// - VERSION-INFO (0..1)
		if (versionInfo != null) {
			
			Node versionInfoTypeNode = VersionInfoTypeNEO.toNode(graphDB, versionInfo);
			registryObjectTypeNode.createRelationshipTo(versionInfoTypeNode, RelationTypes.hasVersion);
			
		}
		
		return registryObjectTypeNode;
		
	}
	
	public static String getNType() {
		return "RegistryObjectType";
	}
}
