package de.kp.registry.server.neo4j.domain.exception;

public class RegistryException extends Exception {

	private static final long serialVersionUID = 3851297543149262459L;
	
	public RegistryException(String message) {
		super(message);
	}
	
	// transfer a Java exception into the respective binding object
	
	public Object toBinding(RegistryException exception) {
		// TODO
		return null;
	}
	
}
