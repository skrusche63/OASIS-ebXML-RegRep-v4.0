package de.kp.registry.server.neo4j.domain.provenance;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.PostalAddressType;
import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class PostalAddressTypeNEO extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws RegistryException {
		
		PostalAddressType postalAddressType = (PostalAddressType)binding;
				
		// - CITY (0..1)
		String city = postalAddressType.getCity();
		
		// - COUNTRY (0..1)
		String country = postalAddressType.getCountry();
		
		// - POSTAL-CODE (0..1)
		String postalCode = postalAddressType.getPostalCode();
		
		// - STATE-OR-PROVINCE (0..1)
		String stateOrProvince = postalAddressType.getStateOrProvince();
		 
		// - STREET (0..1)
		String street = postalAddressType.getStreet();

		// - STREET-NUMBER (0..1)
		String streetNumber = postalAddressType.getStreetNumber();

		// create node from underlying ExtensibleObjectType
		Node postalAddressTypeNode = ExtensibleObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a postalAddressType
		postalAddressTypeNode.setProperty(NEO4J_TYPE, getNType());
		
		// - CITY (0..1)
		if (city != null) postalAddressTypeNode.setProperty(OASIS_RIM_CITY, city);
		
		// - COUNTRY (0..1)
		if (country != null) postalAddressTypeNode.setProperty(OASIS_RIM_COUNTRY, country);
		
		// - POSTAL-CODE (0..1)
		if (postalCode != null) postalAddressTypeNode.setProperty(OASIS_RIM_POSTAL_CODE, postalCode);
		
		// - STATE-OR-PROVINCE (0..1)
		if (stateOrProvince != null) postalAddressTypeNode.setProperty(OASIS_RIM_STATE_OR_PROVINCE, stateOrProvince);
		
		// - STREET (0..1)
		if (street != null) postalAddressTypeNode.setProperty(OASIS_RIM_STREET, street);
		
		// - STREET-NUMBER (0..1)
		if (streetNumber != null) postalAddressTypeNode.setProperty(OASIS_RIM_STREET_NUMBER, streetNumber);

		return postalAddressTypeNode;
		
	}

	public static Object toBinding(Node node) {
			
		PostalAddressType binding = factory.createPostalAddressType();
		binding = (PostalAddressType)ExtensibleObjectTypeNEO.fillBinding(node, binding);

		// - CITY (0..1)
		if (node.hasProperty(OASIS_RIM_CITY)) binding.setCity((String)node.getProperty(OASIS_RIM_CITY));

		// - COUNTRY (0..1)
		if (node.hasProperty(OASIS_RIM_COUNTRY)) binding.setCountry((String)node.getProperty(OASIS_RIM_COUNTRY));

		// - POSTAL-CODE (0..1)
		if (node.hasProperty(OASIS_RIM_POSTAL_CODE)) binding.setCountry((String)node.getProperty(OASIS_RIM_POSTAL_CODE));

		// - STATE-OR-PROVINCE (0..1)
		if (node.hasProperty(OASIS_RIM_STATE_OR_PROVINCE)) binding.setCountry((String)node.getProperty(OASIS_RIM_STATE_OR_PROVINCE));

		// - STREET (0..1)
		if (node.hasProperty(OASIS_RIM_STREET)) binding.setCountry((String)node.getProperty(OASIS_RIM_STREET));

		// - STREET-NUMBER (0..1)
		if (node.hasProperty(OASIS_RIM_STREET_NUMBER)) binding.setCountry((String)node.getProperty(OASIS_RIM_STREET_NUMBER));

		return binding;
		
	}
	
	public static String getNType() {
		return "PostalAddressType";
	}
}
