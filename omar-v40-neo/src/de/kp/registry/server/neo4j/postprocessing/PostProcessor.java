package de.kp.registry.server.neo4j.postprocessing;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.kp.registry.server.neo4j.service.context.RequestContext;
import de.kp.registry.server.neo4j.service.context.ResponseContext;

public class PostProcessor {

	private static PostProcessor instance = new PostProcessor();
	
	private PostProcessor() {
		
	}
	
	public static PostProcessor getInstance() {
		if (instance == null) instance = new PostProcessor();
		return instance;
	}
	
	public void process(RequestContext request, ResponseContext response) {

		ExecutorService executor = Executors.newCachedThreadPool();
		@SuppressWarnings("unused")
		Future<?> f = executor.submit(new PostWorker(request, response));
		
		executor.shutdown();

	}
}
