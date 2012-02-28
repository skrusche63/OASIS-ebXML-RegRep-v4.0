package de.kp.registry.server.neo4j.domain.provenance;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.EmailAddressType;

import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;

public class EmailAddressTypeNEO extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
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

	public static String getNType() {
		return "EmailAddressType";
	}
}
