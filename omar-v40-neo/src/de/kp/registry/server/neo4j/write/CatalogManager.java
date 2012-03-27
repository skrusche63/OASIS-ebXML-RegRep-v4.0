package de.kp.registry.server.neo4j.write;

import de.kp.registry.server.neo4j.service.context.CatalogRequestContext;
import de.kp.registry.server.neo4j.service.context.CatalogResponseContext;
import de.kp.registry.server.neo4j.service.context.ResponseContext;

public class CatalogManager {

	private static CatalogManager instance = new CatalogManager();

	// reference to OASIS ebRIM object factory
	public static org.oasis.ebxml.registry.bindings.rim.ObjectFactory ebRIMFactory = new org.oasis.ebxml.registry.bindings.rim.ObjectFactory();

	// reference to OASIS ebRS object factory
	public static org.oasis.ebxml.registry.bindings.rs.ObjectFactory ebRSFactory = new org.oasis.ebxml.registry.bindings.rs.ObjectFactory();

	private CatalogManager() {		
	}
	
	public static CatalogManager getInstance() {
		if (instance == null) instance = new CatalogManager();
		return instance;
	}

	// this public method is used by the LifecycleManager
	public ResponseContext catalogObjects(CatalogRequestContext request, CatalogResponseContext response) {
		// TODO
		return response;
	}

}
