package de.kp.registry.server.neo4j.domain;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.oasis.ebxml.registry.bindings.rim.InternationalStringType;
import org.oasis.ebxml.registry.bindings.rim.LocalizedStringType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;
import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.common.CanonicalSchemes;

// this is a helper class to support the generation
// of server based registry objects

public class DomainUtil {
	
	// reference to OASIS ebRIM object factory
	public static org.oasis.ebxml.registry.bindings.rim.ObjectFactory ebRIMFactory = new org.oasis.ebxml.registry.bindings.rim.ObjectFactory();
	
	public static Object fillRegistryObjectType(RegistryObjectType binding, Map<String,Object>properties) {
		
		Set<String> keys = properties.keySet();
		
		/********************************************************************
		 * 
		 * REGISTRY OBJECT    REGISTRY OBJECT    REGISTRY OBJECT    REGISTRY
		 * 
		 *******************************************************************/
		
		// - CLASSIFICATION (0..*)

		// NOT USED FOR SERVER GENERATED REGISTRY OBJECT

		
		// - DESCRIPTION (0..1)

		// the description of a registry object may be set from external properties
		String descriptionText = "This instance represents a server generated " + getObjectType(binding) + ".";

		if (keys.contains(NEOBase.OASIS_RIM_DESCRIPTION))
			descriptionText = (String)properties.get(NEOBase.OASIS_RIM_DESCRIPTION);
		
		LocalizedStringType descriptionValue = ebRIMFactory.createLocalizedStringType();

		descriptionValue.setLang(CanonicalConstants.DEFAULT_LANGUAGE);
		descriptionValue.setValue(descriptionText);
		
		InternationalStringType description = ebRIMFactory.createInternationalStringType();
		description.getLocalizedString().add(descriptionValue);
		
		// - EXTERNAL-IDENTIFIER (0..*)

		// NOT USED FOR SERVER GENERATED REGISTRY OBJECT
		
		
		// - EXTERNAL-LINK (0..*)
		
		// NOT USED FOR SERVER GENERATED REGISTRY OBJECT
		
		
		// - LID (0..1)

		String lid = "urn:uuid:" + UUID.randomUUID().toString();	
		binding.setLid(lid);

		
		// - NAME (0..1)
		
		// the name of a registry object may be set from external properties
		String nameText = getObjectType(binding);
		
		if (keys.contains(NEOBase.OASIS_RIM_NAME))
			nameText = (String)properties.get(NEOBase.OASIS_RIM_NAME);

		LocalizedStringType nameValue = ebRIMFactory.createLocalizedStringType();

		nameValue.setLang(CanonicalConstants.DEFAULT_LANGUAGE);
		nameValue.setValue(nameText);

		InternationalStringType name = ebRIMFactory.createInternationalStringType();
		name.getLocalizedString().add(nameValue);
		
		
		// - OBJECT-TYPE (0..1)

		// NOT USED FOR SERVER GENERATED REGISTRY OBJECT
		
		
		// - OWNER (0..1)
		
		// __DESIGN__
		
		// the owner of a server generated registry object instance is the
		// predefined registry operator
		
		String operator = CanonicalConstants.REGISTRY_OPERATOR;
		binding.setOwner(operator);
		
		// - STATUS (0..1)
		
		//  A RegistryObjectType instance MUST have a life cycle status indicator. 
		// The status is assigned by the server
		
		String status = CanonicalSchemes.CANONICAL_STATUS_TYPE_ID_Approved;
		binding.setStatus(status);

		
		// - VERSION-INFO (0..1)

		// NOT USED FOR SERVER GENERATED REGISTRY OBJECT

		
		/********************************************************************
		 * 
		 * IDENTIFIABLE    IDENTIFIABLE    IDENTIFIABLE    IDENTIFIABLE
		 * 
		 *******************************************************************/

		// - ID (1..1)
		
		// the unique identifier for an identifiable is equal to the logical
		// identifier of the respective registry object instance
		
		binding.setId(lid);

		
		/********************************************************************
		 * 
		 * EXTENSIBLE OBJECT    EXTENSIBLE OBJECT    EXTENSIBLE OBJECT
		 * 
		 *******************************************************************/

		// NOT USED FOR SERVER GENERATED REGISTRY OBJECT

		return binding;
		
	}

	public static String getObjectType (Object binding) {
		
		String bindingName = binding.getClass().getName();
		int pos = bindingName.lastIndexOf(".");
		
		return bindingName.substring(pos+1);
		
	}
}
