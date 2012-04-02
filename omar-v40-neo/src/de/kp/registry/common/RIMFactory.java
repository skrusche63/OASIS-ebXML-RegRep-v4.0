package de.kp.registry.common;

import java.util.UUID;

import org.oasis.ebxml.registry.bindings.rim.ClassificationType;

public class RIMFactory {

	// reference to OASIS ebRIM object factory
	private static org.oasis.ebxml.registry.bindings.rim.ObjectFactory ebRIMFactory = new org.oasis.ebxml.registry.bindings.rim.ObjectFactory();

	public static ClassificationType createClassification() {
		
		ClassificationType classification = ebRIMFactory.createClassificationType();
		String uid = "urn:uuid:" + UUID.randomUUID().toString();
		
		classification.setId(uid);
		classification.setLid(uid);
		
		return classification;
		
	}
}
