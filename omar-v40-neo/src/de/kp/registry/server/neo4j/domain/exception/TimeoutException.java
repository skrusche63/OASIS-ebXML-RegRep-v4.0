package de.kp.registry.server.neo4j.domain.exception;

public class TimeoutException extends RegistryException {

	private static final long serialVersionUID = 2911503405830110587L;

	public TimeoutException(String message) {
		super(message);
	}

}
