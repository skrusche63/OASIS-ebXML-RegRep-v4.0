package de.kp.registry.server.neo4j.domain.exception;

public class AuthorizationException extends RegistryException {

	private static final long serialVersionUID = 4951276767121919279L;

	public AuthorizationException(String message) {
		super(message);
	}

}
