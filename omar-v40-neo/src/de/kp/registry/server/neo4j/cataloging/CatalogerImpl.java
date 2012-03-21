package de.kp.registry.server.neo4j.cataloging;

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
