package de.kp.registry.server.neo4j.domain.exception;

public class ObjectExistsException extends RegistryException {

	private static final long serialVersionUID = 6179643024869648883L;

	public ObjectExistsException(String message) {
		super(message);
	}

}
