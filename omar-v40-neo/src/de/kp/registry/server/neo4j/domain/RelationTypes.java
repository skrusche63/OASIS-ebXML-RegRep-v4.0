package de.kp.registry.server.neo4j.domain;

import org.neo4j.graphdb.RelationshipType;

public enum RelationTypes implements RelationshipType {

	hasAction,
	hasAffectedObject,
	hasAffectedObjectRef,
	hasAuditableEvent,
	hasChild,
	hasClassification,
	hasContentVersion,
	hasDeliveryInfo,
	hasDescription,
	hasEmailAddress,
	hasIdentifier,
	hasLink,	
	hasLocaleString,
	hasMember,
	hasName,
	hasParameter,
	hasPostalAddress,
	hasQuery,
	hasQueryExpression,
	hasSelector,
	hasServiceEndpoint,
	hasSlot,
	hasTelephoneNumber,
	hasVersion
	
}
