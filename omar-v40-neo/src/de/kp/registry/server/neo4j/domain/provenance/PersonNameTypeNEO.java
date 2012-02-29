package de.kp.registry.server.neo4j.domain.provenance;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.PersonNameType;

import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;

public class PersonNameTypeNEO extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		PersonNameType personNameType = (PersonNameType)binding;
		
		// - FIRST-NAME (0..1)
		String firstName = personNameType.getFirstName();
		
		// - LAST-NAME (0..1)
		String lastName = personNameType.getLastName();
		
		// - MIDDLE-NAME (0..1)
		String middleName = personNameType.getMiddleName();
		
		// create node from underlying ExtensibleObjectType
		Node personNameTypeNode = ExtensibleObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a personNameType
		personNameTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - FIRST-NAME (0..1)
		if (firstName != null) personNameTypeNode.setProperty(OASIS_RIM_FIRST_NAME, firstName);
		
		// - LAST-NAME (0..1)
		if (lastName != null) personNameTypeNode.setProperty(OASIS_RIM_LAST_NAME, lastName);
		
		// - MIDDLE-NAME (0..1)
		if (middleName != null) personNameTypeNode.setProperty(OASIS_RIM_MIDDLE_NAME, middleName);

		return personNameTypeNode;
		
	}
	
	public static Object toBinding(Node node) {
		
		PersonNameType binding = factory.createPersonNameType();
		binding = (PersonNameType)ExtensibleObjectTypeNEO.fillBinding(node, binding);
		
		// - FIRST-NAME (0..1)
		if (node.hasProperty(OASIS_RIM_FIRST_NAME)) binding.setFirstName((String)node.getProperty(OASIS_RIM_FIRST_NAME));

		// - LAST-NAME (0..1)
		if (node.hasProperty(OASIS_RIM_LAST_NAME)) binding.setLastName((String)node.getProperty(OASIS_RIM_LAST_NAME));

		// - MIDDLE-NAME (0..1)
		if (node.hasProperty(OASIS_RIM_MIDDLE_NAME) ) binding.setLastName((String)node.getProperty(OASIS_RIM_MIDDLE_NAME));
		
		return binding;
		
	}
	
	public static String getNType() {
		return "PersonNameType";
	}

}