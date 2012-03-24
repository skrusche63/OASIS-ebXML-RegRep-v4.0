package de.kp.registry.server.neo4j.service.impl;

import java.util.Set;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.oasis.ebxml.registry.bindings.query.QueryRequest;
import org.oasis.ebxml.registry.bindings.query.QueryResponse;
import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.common.CredentialInfo;
import de.kp.registry.server.neo4j.authorization.AuthorizationConstants;
import de.kp.registry.server.neo4j.authorization.AuthorizationHandler;
import de.kp.registry.server.neo4j.authorization.AuthorizationResult;
import de.kp.registry.server.neo4j.federation.FederatedReadManager;
import de.kp.registry.server.neo4j.read.ReadManager;
import de.kp.registry.server.neo4j.service.MsgRegistryException;
import de.kp.registry.server.neo4j.service.QueryManager;
import de.kp.registry.server.neo4j.service.context.QueryRequestContext;
import de.kp.registry.server.neo4j.service.context.QueryResponseContext;
import de.kp.registry.server.neo4j.user.UserUtil;

@WebService(name = "QueryManager", serviceName = "QueryManager", portName = "QueryManagerPort", targetNamespace = "urn:oasis:names:tc:ebxml-regrep:wsdl:registry:services:4.0",
endpointInterface = "de.kp.registry.server.neo4j.service.QueryManager")

@HandlerChain(file="handler-chain.xml")

public class QueryManagerImpl implements QueryManager {

	@Resource 
	WebServiceContext wsContext;

	// reference to OASIS ebQuery object factory
	public static org.oasis.ebxml.registry.bindings.query.ObjectFactory ebQueryFactory = new org.oasis.ebxml.registry.bindings.query.ObjectFactory();

	// reference to OASIS ebRIM object factory
	public static org.oasis.ebxml.registry.bindings.rim.ObjectFactory ebRIMFactory = new org.oasis.ebxml.registry.bindings.rim.ObjectFactory();

	// reference to authorization handler
	private static AuthorizationHandler ah = AuthorizationHandler.getInstance();

	public QueryManagerImpl() {
	}
	
	public QueryResponse executeQuery(QueryRequest request) throws MsgRegistryException {

		QueryRequestContext queryRequest = new QueryRequestContext(request);
		
		// add SAML assertion to remove request
		MessageContext context = wsContext.getMessageContext();
		queryRequest.setCredentialInfo((CredentialInfo)context.get(CanonicalConstants.CREDENTIAL_INFO));
		
		// set caller's user to the query request
		UserUtil.setCallersUser(queryRequest);
		
		// build request response
		QueryResponseContext queryResponse = new QueryResponseContext(request.getId());
						
		// Attribute federated – This optional attribute specifies that the server must 
		// process this query as a federated query. By default its value is false. 
		
		// This value MUST be false when a server routes a federated query to another 
		// server. This is to avoid an infinite loop in federated query processing.
		
		Boolean federated = request.isFederated();
				
		// as a first step, we distinguish between a federated
		// query request and a request for the local registry
		
		if (federated == true) {
			
			FederatedReadManager rm = FederatedReadManager.getInstance();
			queryResponse = rm.executeQuery(queryRequest, queryResponse);

			return queryResponse.getQueryResponse();

		} else {
			
			ReadManager rm = ReadManager.getInstance();
			queryResponse = rm.executeQuery(queryRequest, queryResponse);

			// Authorization of QueryResponse
			AuthorizationResult authRes = ah.authorizeQueryResponse(queryResponse);
			String result = authRes.getResult();
			
			//__DESIGN__
			
			// in case of a query request, authorization of the outgoing 
			// response is thought of some kind of filter, i.e. no exception
			// is thrown, but only those registry objects are returned, that
			// match the actual authorization policies
			
			if (result.equals(AuthorizationConstants.PERMIT_ALL)) {			

				// in this case, all registry objects or object refs associated 
				// with this request are returned to the client
				
				return queryResponse.getQueryResponse();
				
			} else if (result.equals(AuthorizationConstants.PERMIT_SOME)) {
				
				// this is case, only those registry objects or object refs are
				// returned to the client, that match the authorization policies
				Set<String> deniedResources = authRes.getDenied();

				queryResponse.removeObjectRefAll(deniedResources);
				queryResponse.removeRegistryObjectAll(deniedResources);
				
			} else if (result.equals(AuthorizationConstants.PERMIT_NONE)) {
				
				// in this case we have to exclude all registry objects or
				// object refs from the respective result
				queryResponse.clearObjectRef();
				queryResponse.clearRegistryObject();
				
				return queryResponse.getQueryResponse();
				
			}

			return null;

		}
		
	}

}
