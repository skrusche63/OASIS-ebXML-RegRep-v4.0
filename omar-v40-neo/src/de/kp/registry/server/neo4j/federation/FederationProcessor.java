package de.kp.registry.server.neo4j.federation;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.oasis.ebxml.registry.bindings.query.QueryResponse;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;
import org.oasis.ebxml.registry.bindings.rim.RegistryType;

import de.kp.registry.common.CanonicalSchemes;
import de.kp.registry.server.neo4j.service.context.QueryRequestContext;
import de.kp.registry.server.neo4j.service.context.QueryResponseContext;

/*
 * __DESIGN__
 * 
 * Federated IterativeQuery: An InterativQuery is sent to
 * the respective federates of a certain federation; finally,
 * the results are added to federated query result, i.e. the
 * result count depends on the number of federates
 */

public class FederationProcessor {

	private static FederationProcessor instance = new FederationProcessor();
	
	private FederationProcessor() {		
	}
	
	public static FederationProcessor getInstance() {
		if (instance == null) instance = new FederationProcessor();
		return instance;
	}
	
	public QueryResponseContext executeQuery(QueryRequestContext request, QueryResponseContext response) {
		
		List<FederationWorker> workers = new ArrayList<FederationWorker>();		
		ExecutorService executor = Executors.newCachedThreadPool();
		
		// retrieve list of federates for this request
		List<RegistryObjectType> federates = getFederates(request);

		for (RegistryObjectType federate:federates) {
			workers.add(new FederationWorker(request, (RegistryType)federate));			
		}

		long timeout = 1000;
		TimeUnit unit = TimeUnit.MILLISECONDS;

        int maxTotalResultCount = -1;

		try {
			
			// this is an optimistic approach for a federated response
			response.setStatus(CanonicalSchemes.CANONICAL_RESPONSE_STATUS_TYPE_ID_Success);
			
			List<Future<QueryResponse>> results = executor.invokeAll(workers, timeout, unit);
			for(Future<QueryResponse> result:results) {
				
				QueryResponse remoteResponse = result.get();
				if (remoteResponse == null) continue;

                int totalResultCount = remoteResponse.getTotalResultCount().intValue();
                if (totalResultCount > maxTotalResultCount) maxTotalResultCount = totalResultCount;

                if ((remoteResponse.getException() != null) && remoteResponse.getException().size() > 0) {
                	
                	response.setStatus(CanonicalSchemes.CANONICAL_RESPONSE_STATUS_TYPE_ID_PartialSuccess);
                    response.getExceptions().addAll(remoteResponse.getException());

                }

                // __DESIGN__
                
                // there is actually no functionality implemented that handles
                // duplicate objectRefs or registryObjects from different federates
                
                List<ObjectRefType> remoteObjectRefs = remoteResponse.getObjectRefList().getObjectRef();
                if (remoteObjectRefs.isEmpty() == false) {
                	response.addObjectRefAll(remoteObjectRefs);
                }
                
                List<RegistryObjectType> remoteRegistryObjects = remoteResponse.getRegistryObjectList().getRegistryObject();
                if (remoteRegistryObjects.isEmpty() == false) {
                	response.addRegistryObjectAll(remoteRegistryObjects);
                }

			}

			// reflect the incoming parameter 'startIndex'
	 		response.setStartIndex(request.getStartIndex());
	 		response.setTotalResultCount(BigInteger.valueOf(maxTotalResultCount));

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			executor.shutdown();
			
		}
		
		return response;		
	
	}
	
	// the list of federates are retrieved from the FederationProvider
	private List<RegistryObjectType> getFederates(QueryRequestContext request) {
		
		FederationProvider fp = new FederationProvider(request);
		return fp.getFederates();
		
	}
}
