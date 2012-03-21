package de.kp.registry.server.neo4j.service.context;

import org.oasis.ebxml.registry.bindings.spi.CatalogObjectsRequest;


// TODO: Should we restrict to client service invocation

public class CatalogerRequestContext extends RequestContext {

	CatalogObjectsRequest request;
	
	public CatalogerRequestContext(CatalogObjectsRequest request) {
		
		this.request = request;

		// - InvocationControlFile
		
		// Specifies an ExtrinsicObject that is used to control the cataloging
		// process in a type specific manner. See Canonical XML Catalogor plugin 
		// for an example. This element MAY be specified by server when sending 
		// the request to the Cataloger plugin if the Cataloger plugin requires 
		// an invocation control file. It SHOULD NOT be specified by the client.
		
		// - ObjectRefList
		
		// Specifies a collection of references to existing RegistryObject instances
		// in the server. A server MUST catalog all objects that are referenced by 
		// this element. This element is typically used when a client initiates the 
		// catalogObjects protocol.

		// - OriginalObjects
		
		// Specifies a collection of RegistryObject instances. A server MUST catalog 
		// all objects that are contained in this element. This element is typically 
		// used when a server initiates the catalogObjects protocol during the processing 
		// of a submitObjects or updateObjects protocol request or when it is delegating 
		// a client initiated catalogObjects protocol request to a Cataloger plugin

		// - Query
		
		// Specifies a query to be invoked. A server MUST catalog all objects that match
		// the specified query. This element is typically used when a client initiates 
		// the catalogObjects protocol.
		
	}
}
