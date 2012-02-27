package de.kp.registry.server.neo4j.domain;

import org.neo4j.graphdb.RelationshipType;

public enum RelationTypes implements RelationshipType {

	hasChild,
	hasClassification,
	hasDescription,
	hasEmailAddress,
	hasIdentifier,
	hasLink,	
	hasLocaleString,
	hasName,
	hasPostalAddress,
	hasSlot,
	hasTelephoneNumber,
	hasVersion
	
}
