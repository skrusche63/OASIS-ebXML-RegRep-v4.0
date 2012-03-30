package de.kp.registry.server.neo4j.event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.kp.registry.server.neo4j.service.context.RequestContext;
import de.kp.registry.server.neo4j.service.context.ResponseContext;

public class EventProcessor {

	private static EventProcessor instance = new EventProcessor();
	
	private EventProcessor() {
		
	}
	
	public static EventProcessor getInstance() {
		if (instance == null) instance = new EventProcessor();
		return instance;
	}
	
	public void process(RequestContext request, ResponseContext response) {

		ExecutorService executor = Executors.newCachedThreadPool();
		@SuppressWarnings("unused")
		Future<?> f = executor.submit(new EventWorker(request, response));
		
		executor.shutdown();

	}
}
