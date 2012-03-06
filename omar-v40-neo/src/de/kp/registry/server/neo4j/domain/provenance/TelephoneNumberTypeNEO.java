package de.kp.registry.server.neo4j.domain.provenance;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.TelephoneNumberType;

import de.kp.registry.server.neo4j.database.ReadManager;
import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.exception.UnresolvedReferenceException;

public class TelephoneNumberTypeNEO extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws RegistryException {
		
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
		if  (type != null) {

			// make sure that the classification node references an existing node within the database
			if (ReadManager.getInstance().findNodeByID(type) == null) 
				throw new UnresolvedReferenceException("[TelephoneNumberType] Classification node with id '" + type + "' does not exist.");		

			telephoneNumberTypeNode.setProperty(OASIS_RIM_TYPE, type);
		}

		return telephoneNumberTypeNode;
		
	}

	// this is a common wrapper to delete a TelephoneNumberType node and all of its dependencies

	public static void removeNode(Node node) {
		node.delete();		
	}

	public static Object toBinding(Node node) {
		
		TelephoneNumberType binding = factory.createTelephoneNumberType();
		binding = (TelephoneNumberType)ExtensibleObjectTypeNEO.fillBinding(node, binding);

		// - AREA-CODE (0..1)
		if (node.hasProperty(OASIS_RIM_AREA_CODE)) binding.setAreaCode((String)node.getProperty(OASIS_RIM_AREA_CODE));

		// - COUNTRY-CODE (0..1)
		if (node.hasProperty(OASIS_RIM_COUNTRY_CODE)) binding.setCountryCode((String)node.getProperty(OASIS_RIM_COUNTRY_CODE));

		// - EXTENSION (0..1)
		if (node.hasProperty(OASIS_RIM_EXTENSION)) binding.setExtension((String)node.getProperty(OASIS_RIM_EXTENSION));
		
		// - NUMBER (0..1)
		if (node.hasProperty(OASIS_RIM_NUMBER)) binding.setNumber((String)node.getProperty(OASIS_RIM_NUMBER));

		// - TYPE (0..1)
		if (node.hasProperty(OASIS_RIM_TYPE)) binding.setType((String)node.getProperty(OASIS_RIM_TYPE));

		return binding;
		
	}

	public static String getNType() {
		return "TelephoneNumberType";
	}
}
