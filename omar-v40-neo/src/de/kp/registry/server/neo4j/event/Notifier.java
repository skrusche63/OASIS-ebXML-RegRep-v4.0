package de.kp.registry.server.neo4j.event;

import org.oasis.ebxml.registry.bindings.rim.NotificationType;

import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public interface Notifier {

	public void notifyTo(NotificationType notification) throws RegistryException;
	
}
