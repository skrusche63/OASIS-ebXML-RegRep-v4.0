package de.kp.registry.server.neo4j.domain.exception;

public class ObjectNotFoundException extends RegistryException {

	private static final long serialVersionUID = -7591317267937380505L;

	public ObjectNotFoundException(String message) {
		super(message);
	}
	
}
