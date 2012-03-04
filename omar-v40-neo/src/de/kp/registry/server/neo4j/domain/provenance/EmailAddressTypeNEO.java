package de.kp.registry.server.neo4j.domain.provenance;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.EmailAddressType;
import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class EmailAddressTypeNEO extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws RegistryException {
		
		EmailAddressType emailAddressType = (EmailAddressType)binding;
		
		// - ADDRESS (1..1)
		String address = emailAddressType.getAddress();
		
		// - TYPE (0..1)
		String type = emailAddressType.getType();

		// create node from underlying ExtensibleObjectType
		Node emailAddressTypeNode = ExtensibleObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe an emailAddressType
		emailAddressTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - ADDRESS (1..1)
		emailAddressTypeNode.setProperty(OASIS_RIM_EMAIL_ADDRESS, address);
		
		// - TYPE (0..1)
		emailAddressTypeNode.setProperty(OASIS_RIM_TYPE, type);
		
		return emailAddressTypeNode;
		
	}
	
	public static Object toBinding(Node node) {
		
		EmailAddressType binding = factory.createEmailAddressType();
		binding = (EmailAddressType)ExtensibleObjectTypeNEO.fillBinding(node, binding);

		// - ADDRESS (1..1)
		binding.setAddress((String)node.getProperty(OASIS_RIM_EMAIL_ADDRESS));
		
		// - TYPE (0..1)
		if (node.hasProperty(OASIS_RIM_TYPE)) binding.setType((String)node.getProperty(OASIS_RIM_TYPE));
		
		return binding;
		
	}	

	public static String getNType() {
		return "EmailAddressType";
	}
}
