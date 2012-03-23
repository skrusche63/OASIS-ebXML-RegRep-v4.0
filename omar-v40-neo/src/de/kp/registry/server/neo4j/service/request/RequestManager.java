package de.kp.registry.server.neo4j.service.request;

import org.oasis.ebxml.registry.bindings.lcm.RemoveObjectsRequest;
import org.oasis.ebxml.registry.bindings.lcm.SubmitObjectsRequest;
import org.oasis.ebxml.registry.bindings.lcm.UpdateObjectsRequest;

public class RequestManager {

	private static RequestManager instance = new RequestManager();

	// reference to OASIS LCM object factory
	public static org.oasis.ebxml.registry.bindings.lcm.ObjectFactory ebLCMFactory = new org.oasis.ebxml.registry.bindings.lcm.ObjectFactory();

	private RequestManager() {
	}
	
	public static RequestManager getInstance() {
		if (instance == null) instance = new RequestManager();
		return instance;
	}
		
	public RemoveObjectsRequest createRemoveObjectsRequest() {		
		return ebLCMFactory.createRemoveObjectsRequest();
	}
	
	public SubmitObjectsRequest createSubmitObjectsRequest() {		
		return ebLCMFactory.createSubmitObjectsRequest();
	}
	
	public UpdateObjectsRequest createUpdateObjectsRequest() {		
		return ebLCMFactory.createUpdateObjectsRequest();
	}

}
