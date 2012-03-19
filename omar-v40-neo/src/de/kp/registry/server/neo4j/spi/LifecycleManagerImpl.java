package de.kp.registry.server.neo4j.spi;

import org.oasis.ebxml.registry.bindings.lcm.RemoveObjectsRequest;
import org.oasis.ebxml.registry.bindings.lcm.SubmitObjectsRequest;
import org.oasis.ebxml.registry.bindings.lcm.UpdateObjectsRequest;
import org.oasis.ebxml.registry.bindings.rs.RegistryResponseType;

import de.kp.registry.server.neo4j.authorization.AuthorizationHandler;
import de.kp.registry.server.neo4j.write.WriteManager;

public class LifecycleManagerImpl {

	// reference to OASIS ebRS object factory
	public static org.oasis.ebxml.registry.bindings.rs.ObjectFactory ebRSFactory = new org.oasis.ebxml.registry.bindings.rs.ObjectFactory();

	// reference to authorization handler
	private static AuthorizationHandler ah = AuthorizationHandler.getInstance();
	
	public LifecycleManagerImpl() {		
	}

	public RegistryResponseType removeObjects(RemoveObjectsRequest request) {
		
		RemoveRequestContext  removeRequest = new RemoveRequestContext(request);
		RemoveResponseContext removeResponse = new RemoveResponseContext(request.getId());

		// Authorization of RemoveObjectsRequest
		boolean result = ah.authorizeRemoveRequest(removeRequest);
		if (result == false) {			
			// TODO
			return null;
		}
						
		WriteManager wm = WriteManager.getInstance();
		removeResponse = (RemoveResponseContext)wm.removeObjects(removeRequest, removeResponse);
		
		return removeResponse.getRegistryResponse();
		
	}
	
	public RegistryResponseType submitObjects(SubmitObjectsRequest request) {
		
		SubmitRequestContext submitRequest = new SubmitRequestContext(request);
		SubmitResponseContext submitResponse = new SubmitResponseContext(request.getId());

		// Authorization of SubmitObjectsRequest
		boolean result = ah.authorizeSubmitRequest(submitRequest);
		if (result == false) {			
			// TODO
			return null;
		}
		
		WriteManager wm = WriteManager.getInstance();
		submitResponse = (SubmitResponseContext) wm.submitObjects(submitRequest, submitResponse);
		
		return submitResponse.getRegistryResponse();
		
	}

	public RegistryResponseType updateObjects(UpdateObjectsRequest request) {

		UpdateRequestContext updateRequest = new UpdateRequestContext(request);
		UpdateResponseContext updateResponse = new UpdateResponseContext(request.getId());

		// Authorization of UpdateObjectsRequest
		boolean result = ah.authorizeUpdateRequest(updateRequest);
		if (result == false) {			
			// TODO
			return null;
		}
		
		WriteManager wm = WriteManager.getInstance();
		updateResponse = (UpdateResponseContext) wm.updateObjects(updateRequest, updateResponse);

		return updateResponse.getRegistryResponse();
		
	}

}
