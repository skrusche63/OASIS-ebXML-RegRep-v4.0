package de.kp.registry.server.neo4j.service.context;

import org.oasis.ebxml.registry.bindings.spi.ValidateObjectsRequest;

// The Validator interface allows the validation of objects published 
// to the server. The interface may be used by clients to validate objects 
// already published to the server or may be used by the server to validate 
// objects during the processing of the submitObjects or updateObjects protocol

public class ValidatorRequestContext extends RequestContext {

	ValidateObjectsRequest request;
	
	public ValidatorRequestContext(ValidateObjectsRequest request) {
		
		this.request = request;

		// - InvocationControlFile
		
		// Specifies an ExtrinsicObject that is used to control the validation
		// process in a type specific manner. See Canonical XML Validator plugin 
		// for an example. This element MAY be specified by server when sending 
		// the request to the Validator plugin if the Validator plugin requires 
		// an invocation control file. It SHOULD NOT be specified by the client.
		
		// - ObjectRefList
		
		// Specifies a collection of references to existing RegistryObject instances
		// in the server. A server MUST validate all objects that are referenced by 
		// this element. This element is typically used when a client initiates the 
		// validateObjects protocol.
		
		// - OriginalObjects
		
		// Specifies a collection of RegistryObject instances. A server MUST validate
		// all objects that are contained in this element. This element is typically 
		// used when a server initiates the validateObjects protocol during the processing 
		// of a submitObjects or updateObjects protocol request or when it is delegating a 
		// client initiated validateObjects protocol request to a Validator plugin.
		
		// - Query
		
		// Specifies a query to be invoked. A server MUST validate all objects that match
		// the specified query. This element is typically used when a client initiates the 
		// validateObjects protocol.
		
	}
}
