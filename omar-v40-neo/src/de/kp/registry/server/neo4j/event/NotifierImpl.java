package de.kp.registry.server.neo4j.event;

import org.oasis.ebxml.registry.bindings.rim.NotificationType;

import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class NotifierImpl implements Notifier {

	private String endpoint;
	
	public NotifierImpl(String endpoint) {
		this.endpoint = endpoint;
	}
	
	String getEndpoint() {
		return this.endpoint;
	}

	@Override
	public void notifyTo(NotificationType notification) throws RegistryException {}
	
}
