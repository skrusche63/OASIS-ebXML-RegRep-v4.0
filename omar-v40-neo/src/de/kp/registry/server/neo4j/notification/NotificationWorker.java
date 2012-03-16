package de.kp.registry.server.neo4j.notification;

import org.oasis.ebxml.registry.bindings.rs.RegistryResponseType;

public class NotificationWorker implements Runnable {
	
	private RegistryResponseType response;
	
	public NotificationWorker(RegistryResponseType response) {
		this.response = response;
	}

	public void run() {
		// TODO Auto-generated method stub		
	}
}
