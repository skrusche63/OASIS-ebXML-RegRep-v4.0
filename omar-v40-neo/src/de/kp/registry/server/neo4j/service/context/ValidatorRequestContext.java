package de.kp.registry.server.neo4j.service.context;

import org.oasis.ebxml.registry.bindings.spi.ValidateObjectsRequest;


public class ValidatorRequestContext extends RequestContext {

	ValidateObjectsRequest request;
	
	public ValidatorRequestContext(ValidateObjectsRequest request) {
		
		this.request = request;
		
	}
}
