package de.kp.registry.server.neo4j.domain.exception;

import java.util.ArrayList;
import java.util.List;
import org.oasis.ebxml.registry.bindings.rs.RegistryExceptionType;

import de.kp.registry.server.neo4j.authorization.AuthorizationResult;
import de.kp.registry.server.neo4j.service.context.ResponseContext;

public class ExceptionManager {

	private static ExceptionManager instance = new ExceptionManager();
	
	private ExceptionManager() {		
	}
	
	public static ExceptionManager getInstance() {
		if (instance == null) instance = new ExceptionManager();
		return instance;
	}
	
	public ResponseContext getAuthExceptionResponse(AuthorizationResult authRes, ResponseContext response) {

		AuthorizationException exception = createAuthException(authRes);
			
		response.addException(exception);
		return response;
			
	}

	public AuthorizationException createAuthException(AuthorizationResult authRes) {
		
		List<String> deniedResources = new ArrayList<String>(authRes.getDenied());

		// __ DESIGN__
		
		// the exception references the first id of the denied resources
		
		String message = "[" + authRes.getRequestType() + "] You are not authorized to perform this request on the following resource: " +
		"" + deniedResources.get(0);

		
		AuthorizationException authException = new AuthorizationException(message);
		return authException;
	}
	
	public RegistryExceptionType toBinding(Object exception) {
		
		if (exception instanceof AuthenticationException) {
			
			AuthenticationException e = (AuthenticationException)exception;
			return (RegistryExceptionType)e.toBinding();
			
		} else if (exception instanceof AuthorizationException) {
			
			AuthorizationException e = (AuthorizationException)exception;
			return (RegistryExceptionType)e.toBinding();
			
		} else if (exception instanceof InvalidRequestException) {
			
			InvalidRequestException e = (InvalidRequestException)exception;
			return (RegistryExceptionType)e.toBinding();
			
		} else if (exception instanceof ObjectExistsException) {
			
			ObjectExistsException e = (ObjectExistsException)exception;
			return (RegistryExceptionType)e.toBinding();
			
		} else if (exception instanceof ObjectNotFoundException) {
			
			ObjectNotFoundException e = (ObjectNotFoundException)exception;
			return (RegistryExceptionType)e.toBinding();
			
		} else if (exception instanceof QuotaExceededException) {
			
			QuotaExceededException e = (QuotaExceededException)exception;
			return (RegistryExceptionType)e.toBinding();
			
		} else if (exception instanceof ReferencesExistException) {
			
			ReferencesExistException e = (ReferencesExistException)exception;
			return (RegistryExceptionType)e.toBinding();
			
		} else if (exception instanceof TimeoutException) {
			
			TimeoutException e = (TimeoutException)exception;
			return (RegistryExceptionType)e.toBinding();
			
		} else if (exception instanceof UnresolvedReferenceException) {
			
			UnresolvedReferenceException e = (UnresolvedReferenceException)exception;
			return (RegistryExceptionType)e.toBinding();
			
		} else if (exception instanceof UnsupportedCapabilityException) {

			UnsupportedCapabilityException e = (UnsupportedCapabilityException)exception;
			return (RegistryExceptionType)e.toBinding();
			
		}

		return null;
	}
}
