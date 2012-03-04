package de.kp.registry.server.neo4j.domain.exception;

public class UnresolvedReferenceException extends RegistryException {

	private static final long serialVersionUID = -6585380028502494331L;

	public UnresolvedReferenceException(String message) {
		super(message);
	}
	
}
