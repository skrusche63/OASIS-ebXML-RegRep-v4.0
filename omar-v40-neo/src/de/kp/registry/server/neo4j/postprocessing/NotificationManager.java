package de.kp.registry.server.neo4j.postprocessing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.Node;
import org.oasis.ebxml.registry.bindings.rim.ActionType;
import org.oasis.ebxml.registry.bindings.rim.AuditableEventType;
import org.oasis.ebxml.registry.bindings.rim.DeliveryInfoType;
import org.oasis.ebxml.registry.bindings.rim.NotificationType;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefListType;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;
import org.oasis.ebxml.registry.bindings.rim.QueryDefinitionType;
import org.oasis.ebxml.registry.bindings.rim.QueryType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;
import org.oasis.ebxml.registry.bindings.rim.StringQueryExpressionType;
import org.oasis.ebxml.registry.bindings.rim.SubscriptionType;

import de.kp.registry.client.service.impl.NotificationListenerImpl;
import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.common.CanonicalSchemes;
import de.kp.registry.common.ConnectionImpl;
import de.kp.registry.common.CredentialInfo;
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

	// reference to OASIS ebRIM object factory
	private static org.oasis.ebxml.registry.bindings.rim.ObjectFactory ebRIMFactory = new org.oasis.ebxml.registry.bindings.rim.ObjectFactory();
	
	private NotificationManager() {	
	}
	
	public static NotificationManager getInstance() {
		if (instance == null) instance = new NotificationManager();
		return instance;
	}
		
	public void notify(RequestContext request, ResponseContext response) {

	   	CredentialInfo credentialInfo = request.getCredentialInfo();
    	if (credentialInfo == null) return;

		List<NotificationType> notifications = getNotifications(response);
		if (notifications == null) return;

		// invoke registry notification listener to handle the list of
		// created notifications
		
		ConnectionImpl remoteConnection = new ConnectionImpl();

		remoteConnection.setRegistryUrl(CanonicalConstants.NOTIFICATION_LISTENER_URL);
		remoteConnection.setCredentialInfo(credentialInfo);

		// invoke client side notification listener
		NotificationListenerImpl listener = new NotificationListenerImpl(remoteConnection);

		for (NotificationType notification:notifications) {
			listener.onNotification(notification);
		}
		
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
		String notificationOption = deliveryInfo.get(0).getNotificationOption();
		
		List<AuditableEventType> StrippedAuditableEvents = getAuditableEvents(auditableEvents, notificationOption, nodes);
		return createNotificationInternal(subscription, StrippedAuditableEvents);
		
	}

	private NotificationType createNotificationInternal(SubscriptionType subscription, List<AuditableEventType> auditableEvents) {
		return null;
	}
	
	// this method retrieves all auditable events that match with the
	// nodes of a certain subscription; after having identified these
	// events, the affected objects (or references) MUST be reduced
	// to the objects referenced by the respective nodes
	
	private List<AuditableEventType> getAuditableEvents(List<AuditableEventType> auditableEvents, String notificationOption, Iterator<Node> nodes) {

		// build a reference set for the affected objects
		// that must be preserved for notification
		
		Set<String> refs = new HashSet<String>();
		
		while (nodes.hasNext()) {

			Node node = nodes.next();
			String nid = (String)node.getProperty(NEOBase.OASIS_RIM_ID);
		
			refs.add(nid);
			
		}

		List<AuditableEventType> reducedAuditableEvents = new ArrayList<AuditableEventType>();
		for (AuditableEventType auditableEvent:auditableEvents) {
			
			AuditableEventType reducedAuditableEvent = reduceAuditableEvent(auditableEvent, notificationOption, refs);
			if (reducedAuditableEvent != null) reducedAuditableEvents.add(reducedAuditableEvent);
		}
		
		return reducedAuditableEvents;
		
	}
	
	/*
	 * Unlike an AuditableEvent element that contains all objects affected by it, 
	 * the Event element that goes with a notification MUST only contain objects 
	 * that match the selector query of the SubscriptionType instance. 
	 * 
	 * It has only a subset of affected objects compared to the actual AuditableEvent 
	 * it represents. The subset of affected objects MUST be those that match the selector 
	 * query for the subscription.
	*/
	
	private AuditableEventType reduceAuditableEvent(AuditableEventType sourceEvent, String notificationOption, Set<String> refs) {
		
		AuditableEventType targetEvent = ebRIMFactory.createAuditableEventType();
		
		// - ACTION (1..*)
		
		List<ActionType> reducedActions = new ArrayList<ActionType>();
		
		List<ActionType> actions = sourceEvent.getAction();
		for (ActionType action:actions) {
			
			ActionType reducedAction = reduceAction(action, notificationOption, refs);
			if (reducedAction != null) reducedActions.add(reducedAction);
		}
		
		// do not sent notifications that refer to empty actions
		if (reducedActions.size() == 0) return null;
		
		targetEvent.getAction().addAll(reducedActions);
		
		// - REQUEST-ID (1..1)

		// clone request-id from source event
		targetEvent.setRequestId(sourceEvent.getRequestId());

		// - TIMESTAMP (1..1)
		
		// clone timestamp from source event
		targetEvent.setTimestamp(sourceEvent.getTimestamp());
		
		// - USER (1..1)
		
		// clone user from source event
		targetEvent.setUser(sourceEvent.getUser());
		
		return targetEvent;
		
	}
	
	// create a new action type with either a reduced list of affected objects or object refs
	
	private ActionType reduceAction(ActionType sourceAction, String notificationOption, Set<String> refs) {
		
		ActionType targetAction = ebRIMFactory.createActionType();
		
		ObjectRefListType affectedObjectRefs = sourceAction.getAffectedObjectRefs();

		/* 
		 * The Action elements within the Event element MUST contain a RegistryObjectList 
		 * element if subscription'snotificationOption is “Push”.
		 */

		// - AFFECTED-OBJECT (0..1)

		if (notificationOption.equals(CanonicalSchemes.CANONICAL_NOTIFICATION_OPTION_TYPE_ID_Push)) {

			List<RegistryObjectType> reducedRegistryObjects = reduceRegistryObjects(affectedObjectRefs, refs);
			if (reducedRegistryObjects != null) {
			
				targetAction.setAffectedObjects(ebRIMFactory.createRegistryObjectListType());
				targetAction.getAffectedObjects().getRegistryObject().addAll(reducedRegistryObjects);
				
			}

		}
		
		// - AFFECTED-OBJECT-REF (0..1)

		/*
		 * The Action elements within the Event element MUST contain a RegistryObjectRefList 
		 * element if subscription's notificationOption is “Pull”. 
		 */

		if (notificationOption.equals(CanonicalSchemes.CANONICAL_NOTIFICATION_OPTION_TYPE_ID_Pull)) {

			List<ObjectRefType> reducedObjectRefs = reduceObjectRefs(affectedObjectRefs, refs);
			if (reducedObjectRefs != null) {
			
				targetAction.setAffectedObjectRefs(ebRIMFactory.createObjectRefListType());
				targetAction.getAffectedObjectRefs().getObjectRef().addAll(reducedObjectRefs);

			}
		}
		
		// we do not support action that reference no registry object
		if (targetAction.getAffectedObjects().getRegistryObject().size() == 0 && targetAction.getAffectedObjectRefs().getObjectRef().size() == 0) return null;
		
		// - EVENT-TYPE (1..1)

		// clone event from source action
		targetAction.setEventType(sourceAction.getEventType());
		return targetAction;
		
	}

	// this method creates a list of registryObjects that match a list of unique identifiers
	
	private List<RegistryObjectType> reduceRegistryObjects(ObjectRefListType affectedObjectRef, Set<String> refs) {
		
		if (affectedObjectRef == null) return null;
		List<ObjectRefType> objectRefs = affectedObjectRef.getObjectRef();
		
		if (objectRefs.size() == 0) return null;
		
		List<RegistryObjectType> reducedRegistryObjects = new ArrayList<RegistryObjectType>();
		for (ObjectRefType objectRef:objectRefs) {			
			if (refs.contains(objectRef.getId())) {
				
				Node node = rm.findNodeByID(objectRef.getId());
				if (node != null) {
					
					try {
						
						RegistryObjectType registryObject = (RegistryObjectType)rm.toBinding(node, null);
						reducedRegistryObjects.add(registryObject);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return (reducedRegistryObjects.size() == 0) ? null : reducedRegistryObjects;
	
	}

	// this method creates a list of objectRefs that match a list of unique identifiers
	
	private List<ObjectRefType> reduceObjectRefs(ObjectRefListType affectedObjectRef, Set<String> refs) {

		if (affectedObjectRef == null) return null;
		List<ObjectRefType> objectRefs = affectedObjectRef.getObjectRef();
		
		if (objectRefs.size() == 0) return null;
		
		List<ObjectRefType> reducedObjectRefs = new ArrayList<ObjectRefType>();

		for (ObjectRefType objectRef:objectRefs) {			
			if (refs.contains(objectRef.getId())) reducedObjectRefs.add(objectRef);
		}
		
		return (reducedObjectRefs.size() == 0) ? null : reducedObjectRefs;

	}

	
	// this method retrieves all subscriptions that match the valid time window
	
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
