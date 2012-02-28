package de.kp.registry.server.neo4j.domain;

import org.neo4j.graphdb.RelationshipType;

public enum RelationTypes implements RelationshipType {

	hasAction,
	hasAffectedObject,
	hasAffectedObjectRef,
	hasAuditableEvent,
	hasChild,
	hasClassification,
	hasDeliveryInfo,
	hasDescription,
	hasEmailAddress,
	hasIdentifier,
	hasLink,	
	hasLocaleString,
	hasName,
	hasPostalAddress,
	hasSelector,
	hasSlot,
	hasTelephoneNumber,
	hasVersion
	
}
