package de.kp.registry.server.neo4j.service.context;

import org.oasis.ebxml.registry.bindings.spi.CatalogObjectsResponse;

public class CatalogResponseContext extends ResponseContext {

	// reference to OASIS ebSPI object factory
	public static org.oasis.ebxml.registry.bindings.spi.ObjectFactory ebSPIFactory = new org.oasis.ebxml.registry.bindings.spi.ObjectFactory();

	private CatalogObjectsResponse response;
	
	public CatalogResponseContext(String requestId) {
		super(requestId);
		
		this.response = ebSPIFactory.createCatalogObjectsResponse();
		
		// - REQUEST-ID
		
		// The id attribute must be specified by the client to uniquely 
		// identify a request. Its value SHOULD be a UUID URN.	

		this.response.setRequestId(requestId);		

		if (this.response.getRegistryObjectList() == null) this.response.setRegistryObjectList(ebRIMFactory.createRegistryObjectListType());
		if (this.response.getObjectRefList() == null) this.response.setObjectRefList(ebRIMFactory.createObjectRefListType());

	}

	public CatalogObjectsResponse getCatalogResponse() {
		return this.response;
	}
	
}
