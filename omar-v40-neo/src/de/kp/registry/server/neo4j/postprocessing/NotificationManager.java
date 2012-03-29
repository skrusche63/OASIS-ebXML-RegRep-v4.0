package de.kp.registry.server.neo4j.postprocessing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.neo4j.graphdb.Node;
import org.oasis.ebxml.registry.bindings.rim.AuditableEventType;
import org.oasis.ebxml.registry.bindings.rim.DeliveryInfoType;
import org.oasis.ebxml.registry.bindings.rim.NotificationType;
import org.oasis.ebxml.registry.bindings.rim.QueryDefinitionType;
import org.oasis.ebxml.registry.bindings.rim.QueryType;
import org.oasis.ebxml.registry.bindings.rim.StringQueryExpressionType;
import org.oasis.ebxml.registry.bindings.rim.SubscriptionType;

import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.event.SubscriptionTypeNEO;
import de.kp.registry.server.neo4j.read.ReadManager;
import de.kp.registry.server.neo4j.service.context.RequestContext;
import de.kp.registry.server.neo4j.service.context.ResponseContext;

/*
 * This class retrieves all submissions that are potentially affected 
 * by the actual request: Attributes startTime and endTime define the 
 * time window within which the subscription is valid.
 */

public class NotificationManager {

	private ReadManager rm = ReadManager.getInstance();

	private static NotificationManager instance = new NotificationManager();
	
	private NotificationManager() {	
	}
	
	public static NotificationManager getInstance() {
		if (instance == null) instance = new NotificationManager();
		return instance;
	}
		
	public void notify(RequestContext request, ResponseContext response) {

		List<NotificationType> notifications = getNotifications(response);
		if (notifications == null) return;
		
		// TODO		

	}
	
	// this public method retrieves a list of notifications
	// to be sent to the registered notification listener
	
	// __DESIGN__
	
	// notifications are created from the auditable events that
	// have been created with the actual request, i.e. there are
	// no notifications without auditable events created
	
	private List<NotificationType> getNotifications(ResponseContext response) {

		// all subscriptions registered within the OASIS ebXML RegRep, 
		// which have a time window that matches the current time

		List<SubscriptionType> subscriptions = getActiveSubscriptions();
		if (subscriptions == null) return null;

		List<NotificationType> notifications = new ArrayList<NotificationType>();
		
		for (SubscriptionType subscription:subscriptions) {
			
			List<AuditableEventType> auditableEvents = response.getAuditableEvents();
			
			NotificationType notification = createNotification(subscription, auditableEvents);
			if (notification == null) continue;
			
			notifications.add(notification);
			
		}

		return notifications;
		
	}
	
	private NotificationType createNotification(SubscriptionType subscription, List<AuditableEventType> auditableEvents) {

		// - SELECTOR (1..1) 
		
		// Specifies the query that the server MUST invoke to determine whether 
		// an event matches a subscription or not. If the result of the query 
		// contains an object that is affected by an event that has not yet been 
		// delivered to the subscriber then the event matches the subscription
		
		QueryType selector = subscription.getSelector();		
		Iterator<Node> nodes = rm.executeCypherQuery(selector);
		
		List<DeliveryInfoType> deliveryInfo = subscription.getDeliveryInfo();
		
		// __DESIGN__
		
		// actually there is a restricted to a single delivery info for
		// a certain subscription
		
		if (deliveryInfo.size() == 0) return null;

		List<AuditableEventType> StrippedAuditableEvents = getAuditableEvents(auditableEvents, deliveryInfo.get(0), nodes);
		return createNotificationInternal(subscription, StrippedAuditableEvents);
		
	}

	private NotificationType createNotificationInternal(SubscriptionType subscription, List<AuditableEventType> auditableEvents) {
		return null;
	}
	
	// this method retrieves all auditable events that match with the
	// nodes of a certain subscription; after having identified these
	// events, the affected objects (or references) MUST be reduced
	// to the objects referenced by the respective nodes
	
	private List<AuditableEventType> getAuditableEvents(List<AuditableEventType> auditableEvents, DeliveryInfoType deliverInfo, Iterator<Node> nodes) {

		while (nodes.hasNext()) {

			Node node = nodes.next();
			String nid = (String)node.getProperty(NEOBase.OASIS_RIM_ID);
		
			// TODO
			
		}

		
		return null;
		
	}
	
	// this method retrieves all subscriptions that match the
	// valid time window
	
	private List<SubscriptionType> getActiveSubscriptions() {

		List<SubscriptionType> subscriptions = null;
		
		try {
			
			String cypherQuery = getCypherQuery();
			if (cypherQuery == null) return subscriptions;
			
			Iterator<Node> queryResult = rm.executeCypherQuery(cypherQuery);
			while (queryResult.hasNext()) {
				
				Node node = queryResult.next();
				SubscriptionType subscription = (SubscriptionType)SubscriptionTypeNEO.toBinding(node);
				
				if (subscriptions == null) subscriptions = new ArrayList<SubscriptionType>();
				subscriptions.add(subscription);
				
			}
			
			
		} catch (Exception e) {
			// do nothing
		}
		
		return subscriptions;
		
	}
	
	// this method retrieves the cipher statement that is used to
	// determine all subscriptions that match the time conditions
	// described by startTime and endTime
	
	private String getCypherQuery() throws Exception {

		String queryDefinition = CanonicalConstants.QUERY_GetValidSubscriptions;
		
		// retrieve the referenced query definition type
		
		Node node = rm.findNodeByID(queryDefinition);
		if (node == null) return null;
		
		// __DESIGN__
		
		// in order to process a query request, we use the respective 
		// binding of the QueryDefinitionType node
		
		QueryDefinitionType queryDefinitionType = (QueryDefinitionType)rm.toBinding(node, null);		
		StringQueryExpressionType queryExpressionType = (StringQueryExpressionType)queryDefinitionType.getQueryExpression(); 

		if ("CYPHER".equals(queryExpressionType.getQueryLanguage()) == false) return null;
		return queryExpressionType.getValue();

	}

}
