package de.kp.registry.server.neo4j.domain.provenance;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.TelephoneNumberType;

import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;

public class TelephoneNumberTypeNEO extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		TelephoneNumberType telephoneNumberType = (TelephoneNumberType)binding;
		
		// - AREA-CODE (0..1)
		String areaCode = telephoneNumberType.getAreaCode();
		
		// - COUNTRY-CODE (0..1)
		String countryCode = telephoneNumberType.getCountryCode();

		// - EXTENSION (0..1)
		String extension = telephoneNumberType.getExtension();

		// - NUMBER (0..1)
		String number = telephoneNumberType.getNumber();

		// - TYPE (0..1)
		String type = telephoneNumberType.getType();

		// create node from underlying ExtensibleObjectType
		Node telephoneNumberTypeNode = ExtensibleObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe atelephoneNumberType
		telephoneNumberTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - AREA-CODE (0..1)
		if  (areaCode != null) telephoneNumberTypeNode.setProperty(OASIS_RIM_AREA_CODE, areaCode);

		// - COUNTRY-CODE (0..1)
		if  (countryCode != null) telephoneNumberTypeNode.setProperty(OASIS_RIM_COUNTRY_CODE, countryCode);

		// - EXTENSION (0..1)
		if  (extension != null) telephoneNumberTypeNode.setProperty(OASIS_RIM_EXTENSION, extension);

		// - NUMBER (0..1)
		if  (number != null) telephoneNumberTypeNode.setProperty(OASIS_RIM_NUMBER, number);

		// - TYPE (0..1)
		if  (type != null) telephoneNumberTypeNode.setProperty(OASIS_RIM_TYPE, type);

		return telephoneNumberTypeNode;
		
	}

	public static Object toBinding(Node node) {
		
		TelephoneNumberType binding = factory.createTelephoneNumberType();
		binding = (TelephoneNumberType)ExtensibleObjectTypeNEO.fillBinding(node, binding);

		// - AREA-CODE (0..1)
		String areaCode = (String)node.getProperty(OASIS_RIM_AREA_CODE);
		if (areaCode != null) binding.setAreaCode(areaCode);

		// - COUNTRY-CODE (0..1)
		String countryCode = (String)node.getProperty(OASIS_RIM_COUNTRY_CODE);
		if (countryCode != null) binding.setCountryCode(countryCode);

		// - EXTENSION (0..1)
		String extension = (String)node.getProperty(OASIS_RIM_EXTENSION);
		if (extension != null) binding.setExtension(extension);
		
		// - NUMBER (0..1)
		String number = (String)node.getProperty(OASIS_RIM_NUMBER);
		if (number != null) binding.setNumber(number);

		// - TYPE (0..1)
		String type = (String)node.getProperty(OASIS_RIM_TYPE);
		if (type != null) binding.setType(type);

		return binding;
		
	}

	public static String getNType() {
		return "TelephoneNumberType";
	}
}
