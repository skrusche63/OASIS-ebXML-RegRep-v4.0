package de.kp.registry.server.neo4j.domain.core;

import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ClassificationType;
import org.oasis.ebxml.registry.bindings.rim.ExternalIdentifierType;
import org.oasis.ebxml.registry.bindings.rim.ExternalLinkType;
import org.oasis.ebxml.registry.bindings.rim.InternationalStringType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;
import org.oasis.ebxml.registry.bindings.rim.VersionInfoType;

import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.classification.ClassificationTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class RegistryObjectTypeNEO extends IdentifiableTypeNEO {

	// this method creates a new RegistryObjectType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
				
		// create node from underlying IdentifiableType
		Node node = IdentifiableTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a RegistryObjectType
		node.setProperty(NEO4J_TYPE, getNType());
		return fillNodeInternal(graphDB, node, binding, checkReference);
		
	}

	// this method replaces an existing RegistryObjectType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		
		// clear RegistyObjectType specific parameters
		node = clearNode(node);
		
		// clear & fill node with IdentifiableType specific parameters
		node = IdentifiableTypeNEO.fillNode(graphDB, node, binding);
		
		// fill node with RegistryObjectType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 
		
	}

	public static Node clearNode(Node node) {
		
		// - CLASSIFICATION (0..*)
		
		// clear relationship and referenced ClassificationType nodes
		node = NEOBase.clearRelationship(node, RelationTypes.hasClassification, true);
		
		// - DESCRIPTION (0..1)

		// clear relationship and referenced InternationalStringType node
		node = NEOBase.clearRelationship(node, RelationTypes.hasDescription, true);

		// - EXTERNAL-IDENTIFIER (0..*)

		// TODO
		
		// - EXTERNAL-LINK (0..*)

		// TODO
		
		// - LID (0..1)
		if (node.hasProperty(OASIS_RIM_LID)) node.removeProperty(OASIS_RIM_ID);
		
		// - NAME (0..1)

		// clear relationship and referenced InternationalStringType node
		node = NEOBase.clearRelationship(node, RelationTypes.hasName, true);
		
		// - OBJECT-TYPE (0..1)
		if (node.hasProperty(OASIS_RIM_TYPE)) node.removeProperty(OASIS_RIM_TYPE);
		
		// - OWNER (0..1)
		if (node.hasProperty(OASIS_RIM_OWNER)) node.removeProperty(OASIS_RIM_OWNER);
		
		// - STATUS (0..1)
		if (node.hasProperty(OASIS_RIM_STATUS)) node.removeProperty(OASIS_RIM_STATUS);
		
		// - VERSION-INFO (0..1)

		// clear relationship and referenced VersionInfoType node
		node = NEOBase.clearRelationship(node, RelationTypes.hasVersion, true);

		return node;		
	}

	// TODO: checkReference
	
	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

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
		
		// ===== FILL NODE =====
				
		// - CLASSIFICATION (0..*)
		if (classifications.isEmpty() == false) {
			
			for (ClassificationType classification:classifications) {
				
				// a ClassificationType is always created when the respective
				// RegistryObjectType is created, i.e. the ClassificationType
				// is composed within the RegistryObjectType
				
				Node classificationTypeNode = ClassificationTypeNEO.toNode(graphDB, classification);
				node.createRelationshipTo(classificationTypeNode, RelationTypes.hasClassification);
				
			}
		
		}
		
		// - DESCRIPTION (0..1)
		if (registryObjectTypeDescription != null) {
			
			Node internationalStringTypeNode = InternationalStringTypeNEO.toNode(graphDB, registryObjectTypeDescription);
			node.createRelationshipTo(internationalStringTypeNode, RelationTypes.hasDescription);

		}
		
		// - EXTERNAL-IDENTIFIER (0..*)
		if (externalIdentifiers.isEmpty() == false) {
		
			for (ExternalIdentifierType externalIdentifier:externalIdentifiers) {
				
				// an ExternalIdentifierType may already exist when the respective
				// RegistryObjectType is created, i.e. the ExternalIdentifierType
				// is NOT composed within the RegistryObjectType
				
				// TODO: externalIdentifier may already exist
				// TODO: reference to registry object (=parent)
				
				// The value MUST be set by the server if the ExternalLink is 
				// submitted as part of the submission of its parent object

				Node externalIdentifierTypeNode = ExternalIdentifierTypeNEO.toNode(graphDB, externalIdentifier);
				node.createRelationshipTo(externalIdentifierTypeNode, RelationTypes.hasIdentifier);
			
			}
			
		}
		
		// - EXTERNAL-LINK (0..*)
		if (externalLinks.isEmpty()) {
			
			for (ExternalLinkType externalLink:externalLinks) {
			
				// an ExternalLinkType may already exist when the respective
				// RegistryObjectType is created, i.e. the ExternalLinkType
				// is NOT composed within the RegistryObjectType
				
				// TODO: externalLink may already exist
				// TODO: reference to registry object (=parent)
				
				// The value MUST be set by the server if the ExternalLink is 
				// submitted as part of the submission of its parent object
				
				Node externalLinkTypeNode = ExternalLinkTypeNEO.toNode(graphDB, externalLink);
				node.createRelationshipTo(externalLinkTypeNode, RelationTypes.hasLink);
				
			}
			
		}
		
		// - LID (0..1)
		if (registryObjectTypeLid != null) node.setProperty(OASIS_RIM_LID, registryObjectTypeLid);
		
		// - NAME (0..1)
		if (registryObjectTypeName != null) {
			
			Node internationalStringTypeNode = InternationalStringTypeNEO.toNode(graphDB, registryObjectTypeName);
			node.createRelationshipTo(internationalStringTypeNode, RelationTypes.hasName);

		}
		
		// - OBJECT-TYPE (0..1)
		if (registryObjectTypeType != null) node.setProperty(OASIS_RIM_TYPE, registryObjectTypeType);
		
		// - OWNER (0..1)
		if (registryObjectTypeOwner != null) node.setProperty(OASIS_RIM_OWNER, registryObjectTypeOwner);
		
		// - STATUS (0..1)
		if (registryObjectTypeStatus != null) node.setProperty(OASIS_RIM_STATUS, registryObjectTypeStatus);
		
		// - VERSION-INFO (0..1)
		if (versionInfo != null) {
			
			Node versionInfoTypeNode = VersionInfoTypeNEO.toNode(graphDB, versionInfo);
			node.createRelationshipTo(versionInfoTypeNode, RelationTypes.hasVersion);
			
		}
		
		return node;

	}
	
	public static Object toBinding(Node node) {
		return fillBinding(node, factory.createRegistryObjectType());
	}

	public static Object fillBinding(Node node, Object binding) {
		
		RegistryObjectType registryObjectType = (RegistryObjectType)IdentifiableTypeNEO.fillBinding(node, binding);
		Iterable<Relationship> relationships = null;
		
		// - CLASSIFICATION (0..*)
		relationships = node.getRelationships(RelationTypes.hasClassification);
		if (relationships != null) {
		
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node classificationTypeNode = relationship.getEndNode();
				
				ClassificationType classificationType = (ClassificationType)ClassificationTypeNEO.toBinding(classificationTypeNode);				
				registryObjectType.getClassification().add(classificationType);

			}
			
		}

		// - DESCRIPTION (0..1)
		relationships = node.getRelationships(RelationTypes.hasDescription);
		if (relationships != null) {
		
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node internationaStringTypeNode = relationship.getEndNode();
				
				InternationalStringType internationalStringType = (InternationalStringType)InternationalStringTypeNEO.toBinding(internationaStringTypeNode);				
				registryObjectType.setDescription(internationalStringType);

			}
			
		}

		// - EXTERNAL-IDENTIFIER (0..*)
		relationships = node.getRelationships(RelationTypes.hasIdentifier);
		if (relationships != null) {
		
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node externalIdentifierTypeNode = relationship.getEndNode();
				
				ExternalIdentifierType externalIdentifierType = (ExternalIdentifierType)ExternalIdentifierTypeNEO.toBinding(externalIdentifierTypeNode);				
				registryObjectType.getExternalIdentifier().add(externalIdentifierType);

			}
			
		}
		
		// - EXTERNAL-LINK (0..*)
		relationships = node.getRelationships(RelationTypes.hasLink);
		if (relationships != null) {
		
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node externalLinkTypeNode = relationship.getEndNode();
				
				ExternalLinkType externalLinkType = (ExternalLinkType)ExternalLinkTypeNEO.toBinding(externalLinkTypeNode);				
				registryObjectType.getExternalLink().add(externalLinkType);

			}
			
		}
		
		// - LID (0..1)
		if (node.hasProperty(OASIS_RIM_LID)) registryObjectType.setLid((String)node.getProperty(OASIS_RIM_LID));
		
		// - NAME (0..1)
		relationships = node.getRelationships(RelationTypes.hasName);
		if (relationships != null) {
		
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node internationaStringTypeNode = relationship.getEndNode();
				
				InternationalStringType internationalStringType = (InternationalStringType)InternationalStringTypeNEO.toBinding(internationaStringTypeNode);				
				registryObjectType.setName(internationalStringType);

			}
			
		}
		
		// - OBJECT-TYPE (0..1)
		if (node.hasProperty(OASIS_RIM_TYPE)) registryObjectType.setObjectType((String)node.getProperty(OASIS_RIM_TYPE));
		
		// - OWNER (0..1)
		if (node.hasProperty(OASIS_RIM_OWNER)) registryObjectType.setOwner((String)node.getProperty(OASIS_RIM_OWNER));
		
		// - STATUS (0..1)
		if (node.hasProperty(OASIS_RIM_STATUS)) registryObjectType.setStatus((String)node.getProperty(OASIS_RIM_STATUS));
		
		// - VERSION-INFO (0..1)
		relationships = node.getRelationships(RelationTypes.hasVersion);
		if (relationships != null) {
		
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node versionInfoTypeNode = relationship.getEndNode();
				
				VersionInfoType versionInfoType = (VersionInfoType)VersionInfoTypeNEO.toBinding(versionInfoTypeNode);				
				registryObjectType.setVersionInfo(versionInfoType);

			}
			
		}
		
		return registryObjectType;
		
	}
	
	public static String getNType() {
		return "RegistryObjectType";
	}
}
