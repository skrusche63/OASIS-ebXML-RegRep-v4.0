package de.kp.registry.server.neo4j.authorization.xacml;

import java.util.List;

import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;

import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.server.neo4j.authorization.AuthorizationConstants;
import de.kp.registry.server.neo4j.authorization.AuthorizationContext;
import de.kp.registry.server.neo4j.authorization.AuthorizationResult;
import de.kp.registry.server.neo4j.service.context.CatalogRequestContext;
import de.kp.registry.server.neo4j.service.context.QueryResponseContext;
import de.kp.registry.server.neo4j.service.context.RemoveRequestContext;
import de.kp.registry.server.neo4j.service.context.RequestContext;
import de.kp.registry.server.neo4j.service.context.SubmitRequestContext;
import de.kp.registry.server.neo4j.service.context.UpdateRequestContext;

public class PolicyEnforcementPoint {

	private static PolicyEnforcementPoint instance = new PolicyEnforcementPoint();
	
	// reference to the policy decision point
	private static PolicyDecisionPoint pdp = PolicyDecisionPoint.getInstance();
	
	private PolicyEnforcementPoint() {
		
		// initialize policy decision point
		pdp.init();
		
	}
	
	public static PolicyEnforcementPoint getInstance() {
		if (instance == null) instance = new PolicyEnforcementPoint();
		return instance;
	}

	public AuthorizationResult authorizeRequest(RequestContext request, AuthorizationResult authRes) {
		
		String user = authRes.getUser();
		
		// the registry operator is permitted to do any operation
		// on this OASIS ebXML RegRep
		
		if (user.equals(CanonicalConstants.REGISTRY_OPERATOR)) {			
			authRes.setResult(AuthorizationConstants.PERMIT_ALL);
			return authRes;
		}
		
		String requestType = authRes.getRequestType();
		if (requestType.equals(AuthorizationConstants.SUBMIT_REQUEST)) {
			
			AuthorizationContext authCtx = createAuthContext(AuthorizationConstants.SUBMIT_REQUEST);
			authCtx.setSubject(user);
			
			SubmitRequestContext submitRequest = (SubmitRequestContext)request;
			List<RegistryObjectType> registryObjects = submitRequest.getList();
			
			for (RegistryObjectType registryObject:registryObjects) {
				
				String resource = registryObject.getId();
				boolean authorized = authorizeResource(resource, authCtx);
				
				if (authorized == true) {
					authRes.addAuthorized(resource);
					
				} else {
					authRes.addDenied(resource);
					
				}
				
			}
			
		} else if (requestType.equals(AuthorizationConstants.UPDATE_REQUEST)) {
			
			AuthorizationContext authCtx = createAuthContext(AuthorizationConstants.UPDATE_REQUEST);
			authCtx.setSubject(user);
			
			UpdateRequestContext updateRequest = (UpdateRequestContext)request;
			List<ObjectRefType> objectRefs = updateRequest.getList();

			for (ObjectRefType objectRef:objectRefs) {
				
				String resource = objectRef.getId();
				boolean authorized = authorizeResource(resource, authCtx);
				
				if (authorized == true) {
					authRes.addAuthorized(resource);
					
				} else {
					authRes.addDenied(resource);
					
				}
				
			}
			
		} else if (requestType.equals(AuthorizationConstants.REMOVE_REQUEST)) {
			
			AuthorizationContext authCtx = createAuthContext(AuthorizationConstants.REMOVE_REQUEST);
			authCtx.setSubject(user);
			
			RemoveRequestContext removeRequest = (RemoveRequestContext)request;
			List<ObjectRefType> objectRefs = removeRequest.getList();

			for (ObjectRefType objectRef:objectRefs) {
				
				String resource = objectRef.getId();
				boolean authorized = authorizeResource(resource, authCtx);
				
				if (authorized == true) {
					authRes.addAuthorized(resource);
					
				} else {
					authRes.addDenied(resource);
					
				}
				
			}

		} else if (requestType.equals(AuthorizationConstants.CATALOG_REQUEST)) {
			
			AuthorizationContext authCtx = createAuthContext(AuthorizationConstants.CATALOG_REQUEST);
			authCtx.setSubject(user);
			
			CatalogRequestContext catalogRequest = (CatalogRequestContext)request;
			List<ObjectRefType> objectRefs = catalogRequest.getObjectRefs();

			for (ObjectRefType objectRef:objectRefs) {
				
				String resource = objectRef.getId();
				boolean authorized = authorizeResource(resource, authCtx);
				
				if (authorized == true) {
					authRes.addAuthorized(resource);
					
				} else {
					authRes.addDenied(resource);
					
				}
				
			}
			
		}
		
		return authRes;

	}

	public AuthorizationResult authorizeResponse(QueryResponseContext response, AuthorizationResult authRes) {

		String user = authRes.getUser();
		
		// the registry operator is permitted to do any operation
		// on this OASIS ebXML RegRep
		
		if (user.equals(CanonicalConstants.REGISTRY_OPERATOR)) {			
			authRes.setResult(AuthorizationConstants.PERMIT_ALL);
			return authRes;
		}

		AuthorizationContext authCtx = createAuthContext(AuthorizationConstants.QUERY_REQUEST);
		authCtx.setSubject(user);

		List<RegistryObjectType> registryObjects = response.getRegistryObject();
		
		for (RegistryObjectType registryObject:registryObjects) {
			
			String resource = registryObject.getId();
			boolean authorized = authorizeResource(resource, authCtx);
			
			if (authorized == true) {
				authRes.addAuthorized(resource);
				
			} else {
				authRes.addDenied(resource);
				
			}
			
		}
		
		return authRes;
	
	}
	
	// this method invokes the olicy decision point to
	// determine whether the current operation is permitted
	// on the provided resource
	
	private boolean authorizeResource(String resource, AuthorizationContext authCtx) {
		return false;
	}
	
	private AuthorizationContext createAuthContext(String requestType) {
		// TODO
		return new AuthorizationContext(requestType);
	}
		
}
