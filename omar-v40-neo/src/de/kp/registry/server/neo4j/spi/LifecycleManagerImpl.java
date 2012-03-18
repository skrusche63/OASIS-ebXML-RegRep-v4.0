package de.kp.registry.server.neo4j.spi;

import org.oasis.ebxml.registry.bindings.lcm.RemoveObjectsRequest;
import org.oasis.ebxml.registry.bindings.lcm.SubmitObjectsRequest;
import org.oasis.ebxml.registry.bindings.lcm.UpdateObjectsRequest;
import org.oasis.ebxml.registry.bindings.rs.RegistryResponseType;

import de.kp.registry.server.neo4j.write.WriteManager;

public class LifecycleManagerImpl {

	// reference to OASIS ebRS object factory
	public static org.oasis.ebxml.registry.bindings.rs.ObjectFactory ebRSFactory = new org.oasis.ebxml.registry.bindings.rs.ObjectFactory();

	public LifecycleManagerImpl() {		
	}

	public RegistryResponseType removeObjects(RemoveObjectsRequest request) {
		
		RemoveRequestContext  removeContext = new RemoveRequestContext(request);
		RemoveResponseContext removeResponse = new RemoveResponseContext(request.getId());
						
		WriteManager wm = WriteManager.getInstance();
		return wm.removeObjects(removeContext, removeResponse);
		
	}

	/*
	 * The SubmitObjects protocol allows a client to submit RegistryObjects to the server. 
	 * It also allows a client to completely replace existing RegistryObjects in the server.
	 */
	
	public RegistryResponseType submitObjects(SubmitObjectsRequest request) {
		
		SubmitRequestContext submitRequest = new SubmitRequestContext(request);
		SubmitResponseContext submitResponse = new SubmitResponseContext(request.getId());

		WriteManager wm = WriteManager.getInstance();
		return wm.submitObjects(submitRequest, submitResponse);
		
	}

	public RegistryResponseType updateObjects(UpdateObjectsRequest request) {

		UpdateRequestContext updateContext = new UpdateRequestContext(request);
		UpdateResponseContext updateResponse = new UpdateResponseContext(request.getId());
		
		WriteManager wm = WriteManager.getInstance();
		return wm.updateObjects(updateContext, updateResponse);

	}

}
