package de.kp.registry.server.neo4j.service.impl;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.oasis.ebxml.registry.bindings.spi.CatalogObjectsRequest;
import org.oasis.ebxml.registry.bindings.spi.CatalogObjectsResponse;

import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.common.CredentialInfo;
import de.kp.registry.server.neo4j.authorization.AuthorizationConstants;
import de.kp.registry.server.neo4j.authorization.AuthorizationManager;
import de.kp.registry.server.neo4j.authorization.AuthorizationResult;
import de.kp.registry.server.neo4j.domain.exception.ExceptionManager;
import de.kp.registry.server.neo4j.service.Cataloger;
import de.kp.registry.server.neo4j.service.MsgRegistryException;
import de.kp.registry.server.neo4j.service.context.CatalogRequestContext;
import de.kp.registry.server.neo4j.service.context.CatalogResponseContext;
import de.kp.registry.server.neo4j.user.UserUtil;
import de.kp.registry.server.neo4j.write.WriteManager;

@WebService(name = "Cataloger", serviceName = "Cataloger", portName = "CatalogerPort", targetNamespace = "urn:oasis:names:tc:ebxml-regrep:wsdl:registry:services:4.0",
endpointInterface = "de.kp.registry.server.neo4j.service.Cataloger")

@HandlerChain(file="handler-chain.xml")

/*
 * The Cataloger interface allows a client to catalog or index objects 
 * already in the server. The interface may be used by clients to catalog 
 * objects already published to the server or may be used by the server to
 * catalog objects during the processing of the submitObjects or updateObjects
 * protocol.
 * 
 * __DESIGN__
 * 
 * The CatalogerImpl is actually not used by server and therefore restricted
 * to client requests only.
 *
 */

public class CatalogerImpl implements Cataloger {

	@Resource 
	WebServiceContext wsContext;

	// reference to authorization handler
	private static AuthorizationManager ah = AuthorizationManager.getInstance();

	public CatalogerImpl() {
	}

	public CatalogObjectsResponse catalogObjects(CatalogObjectsRequest request) throws MsgRegistryException {
		
		CatalogRequestContext  catalogRequest = new CatalogRequestContext(request);
		
		// add SAML assertion to remove request
		MessageContext context = wsContext.getMessageContext();
		catalogRequest.setCredentialInfo((CredentialInfo)context.get(CanonicalConstants.CREDENTIAL_INFO));
		
		// set caller's user to the catalog request
		UserUtil.setCallersUser(catalogRequest);
		
		// build request response
		CatalogResponseContext catalogResponse = new CatalogResponseContext(request.getId());

		// Authorization of CatalogObjectsRequest
		AuthorizationResult authRes = ah.authorizeCatalogRequest(catalogRequest);
		String result = authRes.getResult();
		
		// __DESIGN__
		
		// CatalogObjectsRequest are authorized for the callers' user 
		// in case of the authorization level 'PERMIT_ALL'
		
		if (result.equals(AuthorizationConstants.PERMIT_ALL)) {			

			WriteManager wm = WriteManager.getInstance();
			catalogResponse = (CatalogResponseContext)wm.catalogObjects(catalogRequest, catalogResponse);

		} else if (result.equals(AuthorizationConstants.PERMIT_SOME)) {

			ExceptionManager em = ExceptionManager.getInstance();
			catalogResponse = (CatalogResponseContext)em.getAuthExceptionResponse(authRes, catalogResponse);
			
		} else if (result.equals(AuthorizationConstants.PERMIT_NONE)) {

			ExceptionManager em = ExceptionManager.getInstance();
			catalogResponse = (CatalogResponseContext)em.getAuthExceptionResponse(authRes, catalogResponse);

		}
		
		return catalogResponse.getCatalogResponse();

	}
	
}
