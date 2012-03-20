package de.kp.registry.server.neo4j.lcm;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.oasis.ebxml.registry.bindings.lcm.RemoveObjectsRequest;
import org.oasis.ebxml.registry.bindings.lcm.SubmitObjectsRequest;
import org.oasis.ebxml.registry.bindings.lcm.UpdateObjectsRequest;
import org.oasis.ebxml.registry.bindings.rs.RegistryResponseType;

import de.kp.registry.server.neo4j.authorization.AuthorizationConstants;
import de.kp.registry.server.neo4j.authorization.AuthorizationHandler;
import de.kp.registry.server.neo4j.authorization.AuthorizationResult;
import de.kp.registry.server.neo4j.domain.exception.ExceptionManager;
import de.kp.registry.server.neo4j.service.LifecycleManager;
import de.kp.registry.server.neo4j.service.MsgRegistryException;
import de.kp.registry.server.neo4j.write.WriteManager;

@WebService(name = "LifecycleManager", serviceName = "LifecycleManager", portName = "LifecycleManagerPort", targetNamespace = "urn:oasis:names:tc:ebxml-regrep:wsdl:registry:services:4.0",
endpointInterface = "de.kp.registry.server.neo4j.service.LifecycleManager")

@HandlerChain(file="handler-chain.xml")

public class LifecycleManagerImpl implements LifecycleManager {

	@Resource 
	WebServiceContext wsContext;

	// reference to OASIS ebRS object factory
	public static org.oasis.ebxml.registry.bindings.rs.ObjectFactory ebRSFactory = new org.oasis.ebxml.registry.bindings.rs.ObjectFactory();

	// reference to authorization handler
	private static AuthorizationHandler ah = AuthorizationHandler.getInstance();
	
	public LifecycleManagerImpl() {		
	}

	public RegistryResponseType removeObjects(RemoveObjectsRequest request) throws MsgRegistryException {
		
		RemoveRequestContext  removeRequest = new RemoveRequestContext(request);
		RemoveResponseContext removeResponse = new RemoveResponseContext(request.getId());

		// Authorization of RemoveObjectsRequest
		AuthorizationResult authRes = ah.authorizeRemoveRequest(removeRequest);
		String result = authRes.getResult();
		
		// __DESIGN__
		
		// RemoveObjectsRequest are authorized for the callers' user 
		// in case of the authorization level 'PERMIT_ALL'
		
		if (result.equals(AuthorizationConstants.PERMIT_ALL)) {			
			
			WriteManager wm = WriteManager.getInstance();
			removeResponse = (RemoveResponseContext)wm.removeObjects(removeRequest, removeResponse);

		} else if (result.equals(AuthorizationConstants.PERMIT_SOME)) {

			ExceptionManager em = ExceptionManager.getInstance();
			removeResponse = (RemoveResponseContext)em.getAuthExceptionResponse(authRes, removeResponse);
			
		} else if (result.equals(AuthorizationConstants.PERMIT_NONE)) {

			ExceptionManager em = ExceptionManager.getInstance();
			removeResponse = (RemoveResponseContext)em.getAuthExceptionResponse(authRes, removeResponse);

		}
		
		return removeResponse.getRegistryResponse();
		
	}
	
	public RegistryResponseType submitObjects(SubmitObjectsRequest request) throws MsgRegistryException {
		
		SubmitRequestContext submitRequest = new SubmitRequestContext(request);
		SubmitResponseContext submitResponse = new SubmitResponseContext(request.getId());

		// Authorization of SubmitObjectsRequest
		AuthorizationResult authRes = ah.authorizeSubmitRequest(submitRequest);
		String result = authRes.getResult();

		// __DESIGN__
		
		// SubmitObjectsRequest are authorized for the callers' user 
		// in case of the authorization level 'PERMIT_ALL'
		
		if (result.equals(AuthorizationConstants.PERMIT_ALL)) {			
			
			WriteManager wm = WriteManager.getInstance();
			submitResponse = (SubmitResponseContext) wm.submitObjects(submitRequest, submitResponse);

		} else if (result.equals(AuthorizationConstants.PERMIT_SOME)) {

			ExceptionManager em = ExceptionManager.getInstance();
			submitResponse = (SubmitResponseContext)em.getAuthExceptionResponse(authRes, submitResponse);
			
		} else if (result.equals(AuthorizationConstants.PERMIT_NONE)) {

			ExceptionManager em = ExceptionManager.getInstance();
			submitResponse = (SubmitResponseContext)em.getAuthExceptionResponse(authRes, submitResponse);
			
		}
		
		return submitResponse.getRegistryResponse();
		
	}

	public RegistryResponseType updateObjects(UpdateObjectsRequest request) throws MsgRegistryException {

		UpdateRequestContext updateRequest = new UpdateRequestContext(request);
		UpdateResponseContext updateResponse = new UpdateResponseContext(request.getId());

		// Authorization of UpdateObjectsRequest
		AuthorizationResult authRes = ah.authorizeUpdateRequest(updateRequest);
		String result = authRes.getResult();

		// __DESIGN__
		
		// UpdateObjectsRequest are authorized for the callers' user 
		// in case of the authorization level 'PERMIT_ALL'
		
		if (result.equals(AuthorizationConstants.PERMIT_ALL)) {			
			
			WriteManager wm = WriteManager.getInstance();
			updateResponse = (UpdateResponseContext) wm.updateObjects(updateRequest, updateResponse);

		} else if (result.equals(AuthorizationConstants.PERMIT_SOME)) {

			ExceptionManager em = ExceptionManager.getInstance();
			updateResponse = (UpdateResponseContext)em.getAuthExceptionResponse(authRes, updateResponse);
			
		} else if (result.equals(AuthorizationConstants.PERMIT_NONE)) {

			ExceptionManager em = ExceptionManager.getInstance();
			updateResponse = (UpdateResponseContext)em.getAuthExceptionResponse(authRes, updateResponse);
			
		}

		return updateResponse.getRegistryResponse();
		
	}

}
