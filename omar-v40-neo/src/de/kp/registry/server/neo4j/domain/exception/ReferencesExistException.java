package de.kp.registry.server.neo4j.domain.exception;

public class ReferencesExistException extends RegistryException {

	private static final long serialVersionUID = -5645048628143463986L;

	public ReferencesExistException(String message) {
		super(message);
	}

}
