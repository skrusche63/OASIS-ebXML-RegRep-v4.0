package de.kp.registry.server.neo4j.notification;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.oasis.ebxml.registry.bindings.rs.RegistryResponseType;

public class NotificationProcessor {

	private static NotificationProcessor instance = new NotificationProcessor();
	
	private NotificationProcessor() {		
	}
	
	public static NotificationProcessor getInstance() {
		if (instance == null) instance = new NotificationProcessor();
		return instance;		
	}
	
	@SuppressWarnings("unused")
	public void notify(RegistryResponseType response) {
		
		ExecutorService executor = Executors.newCachedThreadPool();
		Future<?> f = executor.submit(new NotificationWorker(response));
		
		executor.shutdown();
		
	}
	
}