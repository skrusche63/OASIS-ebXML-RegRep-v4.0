package de.kp.registry.server.neo4j.cataloging.validation;

import org.oasis.ebxml.registry.bindings.spi.ValidateObjectsRequest;

import de.kp.registry.server.neo4j.common.RequestContext;

public class ValidatorRequestContext extends RequestContext {

	ValidateObjectsRequest request;
	
	public ValidatorRequestContext(ValidateObjectsRequest request) {
		
		this.request = request;
		
	}
}
