package de.kp.registry.server.neo4j.notification;

import java.util.List;

import org.oasis.ebxml.registry.bindings.rim.SubscriptionType;

import de.kp.registry.server.neo4j.service.context.RequestContext;
import de.kp.registry.server.neo4j.service.context.ResponseContext;

// the notification mechanism invoked by this worker depends on the
// delivery info assigned to a certain subscription; actually two
// use cases are supported
//
// If the <wsa:Address> element has a rim:endpointType attribute 
// value of “urn:oasis:names:tc:ebxml-regrep:endPointType:rest”, 
// then the server MUST use the specified address as the email 
// address to deliver the Notification via email. This specification 
// does not define how a server is configured to send Notifications
// via email.
//
// If the <wsa:Address> element has a rim:endpointType attribute 
// value of “urn:oasis:names:tc:ebxml-regrep:endPointType:soap”, 
// then the server MUST use the specified address as the web service 
// endpoint URL to deliver the Notification to. The target web service
// in this case MUST implement the Notification-Listener interface.
//
// If the <wsa:Address> element has a rim:endpointType attribute 
// value of “urn:oasis:names:tc:ebxml-regrep:endPointType:plugin”, 
// then the server MUST use the specified address as a Notification 
// plugin identifier and deliver the Notification via local call to 
// the plugin. This specification does not define how a server is 
// configured for Notification plugins.

public class NotificationWorker implements Runnable {
	
	private RequestContext request;
	private ResponseContext response;
	
	public NotificationWorker(RequestContext request, ResponseContext response) {
		
		this.request = request;
		this.response = response;
		
	}

	public void run() {
		
		// retrieve list of valid subscriptions, i.e. all registered subscriptions
		// which have a time window defined (startTime & endTime) that matches the
		// current time
		SubscriptionManager sm = SubscriptionManager.getInstance();
		
		List<SubscriptionType> subscriptions = sm.getSubscriptions();
		if (subscriptions == null) return;
		
		// TODO Auto-generated method stub		
	}
}
