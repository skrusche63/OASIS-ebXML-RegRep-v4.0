package de.kp.registry.server.neo4j.domain.provenance;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.PostalAddressType;
import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;

public class PostalAddressTypeNEO extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) {
		
		PostalAddressType postalAddressType = (PostalAddressType)binding;
				
		// - CITY
		String city = postalAddressType.getCity();
		
		// - COUNTRY
		String country = postalAddressType.getCountry();
		
		// - POSTAL-CODE
		String postalCode = postalAddressType.getPostalCode();
		
		// - STATE-OR-PROVINCE
		String stateOrProvince = postalAddressType.getStateOrProvince();
		
		// - STREET
		String street = postalAddressType.getStreet();

		// - STREET-NUMBER
		String streetNumber = postalAddressType.getStreetNumber();

		// create node from underlying ExtensibleObjectType
		Node postalAddressTypeNode = ExtensibleObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a postalAddressType
		postalAddressTypeNode.setProperty(NEO4J_TYPE, getNType());
		
		// - CITY
		if (city != null) postalAddressTypeNode.setProperty(OASIS_RIM_CITY, city);
		
		// - COUNTRY
		if (country != null) postalAddressTypeNode.setProperty(OASIS_RIM_COUNTRY, country);
		
		// - POSTAL-CODE
		if (postalCode != null) postalAddressTypeNode.setProperty(OASIS_RIM_POSTAL_CODE, postalCode);
		
		// - STATE-OR-PROVINCE
		if (stateOrProvince != null) postalAddressTypeNode.setProperty(OASIS_RIM_STATE_OR_PROVINCE, stateOrProvince);
		
		// - STREET
		if (street != null) postalAddressTypeNode.setProperty(OASIS_RIM_STREET, street);
		
		// - STREET-NUMBER
		if (streetNumber != null) postalAddressTypeNode.setProperty(OASIS_RIM_STREET_NUMBER, streetNumber);

		return postalAddressTypeNode;
		
	}

	public static String getNType() {
		return "PostalAddressType";
	}
}
