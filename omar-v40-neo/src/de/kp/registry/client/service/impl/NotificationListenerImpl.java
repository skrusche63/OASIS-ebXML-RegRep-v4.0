package de.kp.registry.client.service.impl;

import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.oasis.ebxml.registry.bindings.rim.NotificationType;

import de.kp.registry.client.service.NotificationListenerSOAPService;
import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.common.ConnectionImpl;
import de.kp.registry.common.CredentialInfo;
import de.kp.registry.server.neo4j.service.NotificationListener;

public class NotificationListenerImpl {

	private static QName QNAME = new QName("urn:oasis:names:tc:ebxml-regrep:wsdl:registry:services:4.0", "NotificationListener");
	
	private NotificationListenerSOAPService service;
	private NotificationListener port;
	
	private ConnectionImpl connection;
	
	public NotificationListenerImpl(ConnectionImpl connection) {

		// register connection object
		this.connection = connection;
		URL wsdlLocation = this.connection.getNotificationListenerURL();
		
		service = new NotificationListenerSOAPService(wsdlLocation, QNAME);
		port = service.getNotificationListenerPort();
		
	}

	public void onNotification(NotificationType notification) {
		
		// assign SAML credentials to request context; this is a mechanism
		// to share the respective assertion with the SOAP message handler
		
		Map<String, Object> context = ((BindingProvider) port).getRequestContext();
		
		CredentialInfo credentialInfo = new CredentialInfo();
		credentialInfo.setAssertion(this.connection.getAssertion());
		
		context.put(CanonicalConstants.CREDENTIAL_INFO, credentialInfo);
		port.onNotification(notification);
		
	}

}
