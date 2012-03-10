package de.kp.registry.server.neo4j.domain.exception;

public class QuotaExceededException extends RegistryException {

	private static final long serialVersionUID = 2589777785895107317L;

	public QuotaExceededException(String message) {
		super(message);
	}

}
