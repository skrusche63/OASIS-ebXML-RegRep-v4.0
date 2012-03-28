package de.kp.registry.server.neo4j.federation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.oasis.ebxml.registry.bindings.query.QueryResponse;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;
import org.oasis.ebxml.registry.bindings.rim.RegistryType;

import de.kp.registry.server.neo4j.service.context.QueryRequestContext;

public class FederationProcessor {

	private static FederationProcessor instance = new FederationProcessor();
	
	private FederationProcessor() {		
	}
	
	public static FederationProcessor getInstance() {
		if (instance == null) instance = new FederationProcessor();
		return instance;
	}
	
	public QueryResponse executeQuery(QueryRequestContext request, QueryResponse response) {
		
		List<FederationWorker> workers = new ArrayList<FederationWorker>();		
		ExecutorService executor = Executors.newCachedThreadPool();
		
		// retrieve list of federates for this request
		List<RegistryObjectType> federates = getFederates(request);

		for (RegistryObjectType federate:federates) {
			workers.add(new FederationWorker(request, (RegistryType)federate));			
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
	
	// the list of federates are retrieved from the FederationProvider
	private List<RegistryObjectType> getFederates(QueryRequestContext request) {
		
		FederationProvider fp = new FederationProvider(request);
		return fp.getFederates();
		
	}
}
