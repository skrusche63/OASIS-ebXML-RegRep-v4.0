package de.kp.registry.server.neo4j.domain.core;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ExternalIdentifierType;

import de.kp.registry.server.neo4j.domain.exception.RegistryException;


public class ExternalIdentifierTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new ExternalIdentifierType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		ExternalIdentifierType externalIdentifierType = (ExternalIdentifierType)binding;
		
		// - IDENTIFICATION SCHEME (1..1)
		String identificationScheme = externalIdentifierType.getIdentificationScheme();
		
		// - REGISTRY-OBJECT (0..1)
		String parent = externalIdentifierType.getRegistryObject();
		
		// - VALUE (1..1)
		String value = externalIdentifierType.getValue();
		
		// TODO: here we have to determine whether we retrieve the respective node
		// from the database (if it exists) or through creation

		// create node from underlying RegistryObjectType
		Node externalIdentifierTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a ExternalIdentifierType
		externalIdentifierTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - IDENTIFICATION SCHEME (1..1)
		externalIdentifierTypeNode.setProperty(OASIS_RIM_ID_SCHEME, identificationScheme);
		
		// - REGISTRY-OBJECT (0..1)
		if (parent != null) externalIdentifierTypeNode.setProperty(OASIS_RIM_PARENT, parent);

		// - VALUE (1..1)
		externalIdentifierTypeNode.setProperty(OASIS_RIM_VALUE, value);
		
		return externalIdentifierTypeNode;
	
	}

	// this method replaces an existing ExternalIdentifierType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return null;
	}

	public static Node clearNode(Node node) {
		
		// TODO
		return null;
		
	}
	
	public static Object toBinding(Node node) {
		
		ExternalIdentifierType binding = factory.createExternalIdentifierType();
		binding = (ExternalIdentifierType)RegistryObjectTypeNEO.fillBinding(node, binding);

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
