package de.kp.registry.server.neo4j.service.impl;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.oasis.ebxml.registry.bindings.spi.CatalogObjectsRequest;
import org.oasis.ebxml.registry.bindings.spi.CatalogObjectsResponse;

import de.kp.registry.server.neo4j.service.Cataloger;
import de.kp.registry.server.neo4j.service.MsgRegistryException;

@WebService(name = "NotificationListener", serviceName = "NotificationListener", portName = "NotificationListenerPort", targetNamespace = "urn:oasis:names:tc:ebxml-regrep:wsdl:registry:services:4.0",
endpointInterface = "de.kp.registry.server.neo4j.service.NotificationListener")

@HandlerChain(file="handler-chain.xml")

// The Cataloger interface allows a client to catalog or index objects 
// already in the server. The interface may be used by clients to catalog 
// objects already published to the server or may be used by the server to
// catalog objects during the processing of the submitObjects or updateObjects
// protocol.

public class CatalogerImpl implements Cataloger {

	@Resource 
	WebServiceContext wsContext;

	public CatalogerImpl() {
	}

	public CatalogObjectsResponse catalogObjects(CatalogObjectsRequest request) throws MsgRegistryException {
		
		// TODO
		return null;
	}
	
}
