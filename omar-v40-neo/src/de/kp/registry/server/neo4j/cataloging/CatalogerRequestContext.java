package de.kp.registry.server.neo4j.cataloging;

import org.oasis.ebxml.registry.bindings.spi.CatalogObjectsRequest;

import de.kp.registry.server.neo4j.common.RequestContext;

public class CatalogerRequestContext extends RequestContext {

	CatalogObjectsRequest request;
	
	public CatalogerRequestContext(CatalogObjectsRequest request) {
		
		this.request = request;
		
	}
}
