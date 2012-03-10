package de.kp.registry.server.neo4j.domain.exception;

public class InvalidRequestException extends RegistryException {

	private static final long serialVersionUID = -446758784114238596L;

	public InvalidRequestException(String message) {
		super(message);
	}

}
