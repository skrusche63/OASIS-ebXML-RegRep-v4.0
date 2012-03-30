package de.kp.registry.server.neo4j.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceContext;

import org.neo4j.graphdb.Node;
import org.oasis.ebxml.registry.bindings.rim.DeliveryInfoType;
import org.oasis.ebxml.registry.bindings.rim.NotificationType;
import org.oasis.ebxml.registry.bindings.rim.SubscriptionType;
import org.w3._2005._08.addressing.AttributedURIType;
import org.w3._2005._08.addressing.EndpointReferenceType;

import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.common.CanonicalSchemes;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.event.MailNotifierImpl;
import de.kp.registry.server.neo4j.event.Notifier;
import de.kp.registry.server.neo4j.event.PluginNotifierImpl;
import de.kp.registry.server.neo4j.event.RestNotifierImpl;
import de.kp.registry.server.neo4j.event.SoapNotifierImpl;
import de.kp.registry.server.neo4j.read.ReadManager;
import de.kp.registry.server.neo4j.service.NotificationListener;

@WebService(name = "NotificationListener", serviceName = "NotificationListener", portName = "NotificationListenerPort", targetNamespace = "urn:oasis:names:tc:ebxml-regrep:wsdl:registry:services:4.0",
endpointInterface = "de.kp.registry.server.neo4j.service.NotificationListener")

@HandlerChain(file="handler-chain.xml")

public class NotificationListenerImpl implements NotificationListener {

	@Resource 
	WebServiceContext wsContext;

	private ReadManager rm = ReadManager.getInstance();
	
	public void onNotification(NotificationType notification) {
		
		// - SUBSCRIPTION 1..1)
		String sid = notification.getSubscription();
		
		// determine delivery info for the notification
		Node node = rm.findNodeByID(sid);
		if (node == null) return;
		
		DeliveryInfoType deliveryInfo = null;
		
		try {
		
			String language = null;
			SubscriptionType subscription = (SubscriptionType)rm.toBinding(node, language);

			List<DeliveryInfoType> deliveryInfos = subscription.getDeliveryInfo();
			deliveryInfo = (deliveryInfos.size() == 0) ? null : deliveryInfos.get(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		// __DESIGN__
		
		// the actual implementations requires that a deliveryInfo MUST be set for
		// a certain subscription instance; in addition, the notificationInterval
		// is not supported. This is diffent to the OASIS ebXML RegRep v4.0 RIM
		// specification.
		
		if (deliveryInfo == null) return;
		
		EndpointReferenceType endpointReference = deliveryInfo.getNotifyTo();
		AttributedURIType wsaAddress = endpointReference.getAddress();
		
		/*
		 * Element NotifyTo specifies the endpoint reference for the endpoint where 
		 * the server should deliver notifications for the Subscription.
		 * 
		 * - The type of this element is wsa:EndpointReferenceType as defined by [WSA-Core]
		 * 
		 * - The NotifyTo element has a <wsa:Address> sub-element
		 * 
		 * - The content of the <wsa:Address> element is a string representing the endpoint 
		 *   address which SHOULD be a URI
		 *   
		 * - The type of endpoint (SOAP, REST, email, ...) is indicated by an extension 
		 *   attribute rim:endpointType defined on the <wsa:Address> element as follows:
		 * 
		 *   - If endpoint is a SOAP web service then the rim:endpointType attribute value 
		 *     MUST be “urn:oasis:names:tc:ebxml-regrep:endPointType:soap”
		 *     
		 *   - If endpoint is a REST web service then the rim:endpointType attribute value 
		 *     MUST be “urn:oasis:names:tc:ebxml-regrep:endPointType:rest”
		 *     
		 *   - If endpoint is an email address then the rim:endpointType attribute value 
		 *     MUST be “urn:oasis:names:tc:ebxml-regrep:endPointType:mail”
		 *     
		 *   - If endpoint is a software plugin then the rim:endpointType attribute value 
		 *     MUST be “urn:oasis:names:tc:ebxml-regrep:endPointType:plugin”
		 */
		
		String endpoint = wsaAddress.getValue();
		
		Map<QName, String> attributes = wsaAddress.getOtherAttributes();
		String endpointType = attributes.get(CanonicalConstants.ENDPOINT_TYPE_QNAME);

		if (endpointType == null) return;
		
		Notifier notifier = null;
		
		if (endpointType.equals(CanonicalSchemes.CANONICAL_ENDPOINT_TYPE_ID_Soap)) {
			notifier = new SoapNotifierImpl(endpoint);
			
		} else if (endpointType.equals(CanonicalSchemes.CANONICAL_ENDPOINT_TYPE_ID_Rest)) {
			notifier = new RestNotifierImpl(endpoint);

		} else if (endpointType.equals(CanonicalSchemes.CANONICAL_ENDPOINT_TYPE_ID_Mail)) {
			notifier = new MailNotifierImpl(endpoint);

		} else if (endpointType.equals(CanonicalSchemes.CANONICAL_ENDPOINT_TYPE_ID_Plugin)) {
			notifier = new PluginNotifierImpl(endpoint);
			
		}
		
		if (notifier == null) return;
		
		try {
			notifier.notifyTo(notification);

		} catch (RegistryException e) {
			e.printStackTrace();

		}
		 
	}

}
