package de.kp.registry.server.neo4j.domain.exception;

import org.oasis.ebxml.registry.bindings.rs.ObjectFactory;
import org.oasis.ebxml.registry.bindings.rs.RegistryExceptionType;

public class RegistryException extends Exception {

	private static final long serialVersionUID = 3851297543149262459L;
	
	// reference to OASIS ebRIM object factory
	public static ObjectFactory ebRSFactory = new ObjectFactory();

	public RegistryException(String message) {
		super(message);
	}
	
	// transfer a Java exception into the respective binding object
	
	public Object toBinding() {

		RegistryExceptionType binding = ebRSFactory.createRegistryExceptionType();
		
		// - CODE (0..1)
		
		// The code attribute value may be used by a server to provide an error code or
		// identifier for an Exception.		
		
		// - DETAIL (0..1)

		// The detail attribute value may be used by a server to provide any detailed 
		// information such as a stack trace for an Exception.
		
		// - MESSAGE (1..1)
		
		// The message attribute value MUST be used by a server to provide a brief
		// message summarizing an Exception		

		binding.setMessage(this.getMessage());
		
		// - SEVERITY (0..1)

		// The severity attribute value provides a severity level for the exception. 
		// Its value SHOULD reference a ClassificationNode within the canonical 
		// ErrorSeverityType ClassificationScheme.
		
		return binding;
				
	}
	
}
