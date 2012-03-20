package de.kp.registry.server.neo4j.federation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.oasis.ebxml.registry.bindings.query.QueryResponse;
import org.oasis.ebxml.registry.bindings.rim.RegistryType;

import de.kp.registry.server.neo4j.qm.QueryRequestContext;

public class FederationProcessor {

	private static FederationProcessor instance = new FederationProcessor();
	
	private FederationProcessor() {		
	}
	
	public static FederationProcessor getInstance() {
		if (instance == null) instance = new FederationProcessor();
		return instance;
	}
	
	public QueryResponse executeQuery(QueryRequestContext context, QueryResponse response) {
		
		List<FederationWorker> workers = new ArrayList<FederationWorker>();		
		ExecutorService executor = Executors.newCachedThreadPool();
		
		// retrieve list of federates for this request
		List<RegistryType> federates = getFederates(context);

		for (RegistryType federate:federates) {
			workers.add(new FederationWorker(context, federate));			
		}

		long timeout = 1000;
		TimeUnit unit = TimeUnit.MILLISECONDS;
			
		try {
			
			List<Future<QueryResponse>> results = executor.invokeAll(workers, timeout, unit);
			for(Future<QueryResponse> result:results){
				// TODO
			}
	    
		} catch (Exception e) {
			// TODO
		} finally {
			executor.shutdown();
			
		}
		// TODO: retrieve task results
		
		
		return response;		
	
	}
	
	private List<RegistryType> getFederates(QueryRequestContext context) {
		return null;
	}
}
