package de.kp.registry.server.neo4j.service.impl;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.oasis.ebxml.registry.bindings.rim.NotificationType;

import de.kp.registry.server.neo4j.service.NotificationListener;

@WebService(name = "Cataloger", serviceName = "Cataloger", portName = "CatalogerPort", targetNamespace = "urn:oasis:names:tc:ebxml-regrep:wsdl:registry:services:4.0",
endpointInterface = "de.kp.registry.server.neo4j.service.Cataloger")

@HandlerChain(file="handler-chain.xml")

public class NotificationListenerImpl implements NotificationListener {

	@Resource 
	WebServiceContext wsContext;

	public void onNotification(NotificationType notification) {
		// TODO
	}

}
