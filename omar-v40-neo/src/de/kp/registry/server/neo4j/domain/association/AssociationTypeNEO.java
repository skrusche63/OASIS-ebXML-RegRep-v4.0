package de.kp.registry.server.neo4j.domain.association;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.AssociationType;

import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;

public class AssociationTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new AssociationType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws Exception {
		
		AssociationType associationType = (AssociationType)binding;
		
		// - SOURCE-OBJECT (1..1)
		String sourceObject = associationType.getSourceObject();
		
		// - TARGET-OBJECT (1..1)
		String targetObject = associationType.getTargetObject();
		
		// - TYPE (1..1)
		String type = associationType.getType();
		
		// create node from underlying RegistryObjectType
		Node associationTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe an AssociationType
		associationTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - SOURCE-OBJECT (1..1)
		associationTypeNode.setProperty(OASIS_RIM_SOURCE, sourceObject);
		
		// - TARGET-OBJECT (1..1)
		associationTypeNode.setProperty(OASIS_RIM_TARGET, targetObject);

		// - TYPE (1..1)
		associationTypeNode.setProperty(OASIS_RIM_TYPE, type);
		
		return associationTypeNode;

	}

	// this method replaces an existing AssociationType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws Exception {		
		return null;
	}

	public static Object toBinding(Node node) {
	
		AssociationType binding = factory.createAssociationType();
		binding = (AssociationType)RegistryObjectTypeNEO.fillBinding(node, binding);

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
