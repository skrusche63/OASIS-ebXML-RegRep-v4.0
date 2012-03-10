package de.kp.registry.server.neo4j.domain.exception;

public class AuthenticationException extends RegistryException {

	private static final long serialVersionUID = -4931567547750908740L;

	public AuthenticationException(String message) {
		super(message);
	}

}
