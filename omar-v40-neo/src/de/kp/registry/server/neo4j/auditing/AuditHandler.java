package de.kp.registry.server.neo4j.auditing;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AuditHandler {

	private static AuditHandler instance = new AuditHandler();
	
	private AuditHandler() {}
	
	public static AuditHandler getInstance() {
		if (instance == null) instance = new AuditHandler();
		return instance;
	}
	
	@SuppressWarnings("unused")
	public void audit(AuditContext context) {
		
		ExecutorService executor = Executors.newCachedThreadPool();
		Future<?> f = executor.submit(new AuditWorker(context));
		
		executor.shutdown();
		
	}

}
