package de.kp.registry.server.neo4j.auditing;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AuditProcessor {

	private static AuditProcessor instance = new AuditProcessor();
	
	private AuditProcessor() {}
	
	public static AuditProcessor getInstance() {
		if (instance == null) instance = new AuditProcessor();
		return instance;
	}
	
	@SuppressWarnings("unused")
	public void audit(AuditContext context) {
		
		ExecutorService executor = Executors.newCachedThreadPool();
		Future<?> f = executor.submit(new AuditWorker(context));
		
		executor.shutdown();
		
	}

}
