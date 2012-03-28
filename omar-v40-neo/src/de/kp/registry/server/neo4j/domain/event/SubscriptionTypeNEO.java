package de.kp.registry.server.neo4j.domain.event;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.DeliveryInfoType;
import org.oasis.ebxml.registry.bindings.rim.QueryType;
import org.oasis.ebxml.registry.bindings.rim.SubscriptionType;

import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.query.QueryTypeNEO;
import de.kp.registry.server.neo4j.util.CalendarUtil;

public class SubscriptionTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new SubscriptionType node within database
	
	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {

		// create node from underlying RegistryObjectType
		Node node = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a SubscriptionType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);
		
	}

	// this method replaces an existing SubscriptionType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier

	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return fillNode(graphDB, node, binding, checkReference, false);
	}
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference, boolean excludeVersion) throws RegistryException {
		
		// clear SubscriptionType specific parameters
		node = clearNode(node, excludeVersion);
		
		// clear & fill node with RegistryObjectType specific parameters
		node = RegistryObjectTypeNEO.fillNode(graphDB, node, binding, checkReference, excludeVersion);
		
		// fill node with SubscriptionType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 
	}

	public static Node clearNode(Node node, boolean excludeVersion) {
		
		// - DELIVER-INFO (0..*)
		
		// __DESIGN__
		
		// DeliveryInfoType nodes are an intrinsic part of a SubscriptionType
		// and are therefore removed in addition to the respective relationships

		// clear relationship and referencedDeliveryInfoType nodes
		node = NEOBase.clearRelationship(node, RelationTypes.hasDeliveryInfo, true);

		// - ENDTIME (0..1)
		if (node.hasProperty(OASIS_RIM_ENDTIME)) node.removeProperty(OASIS_RIM_ENDTIME);

		// - NOTIFICATION-INTERVAL (0..1)
		if (node.hasProperty(OASIS_RIM_NOTIFICATION_INTERVAL)) node.removeProperty(OASIS_RIM_NOTIFICATION_INTERVAL);

		// - SELECTOR (1..1)

		// __DESIGN__
		
		// A QueryType node is NOT an intrinsic information
		// that is ultimately related with a SubscriptionType node

		// clear relationship only
		node = NEOBase.clearRelationship(node, RelationTypes.hasSelector, false);

		// - STARTTIME (0..1)
		if (node.hasProperty(OASIS_RIM_STARTTIME)) node.removeProperty(OASIS_RIM_STARTTIME);
		
		return node;
		
	}

	// this is a common wrapper to delete a SubscriptionType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear SubscriptionType specific parameters
		node = clearNode(node, false);
		
		// clear node fromRegistryObjectType specific parameters and remove
		RegistryObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);

	}
	
	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		// parameter 'checkReference' must not be evaluated for SubscriptionTypeInfo nodes,
		// as the respective references all refer to ExtensibleObjectType nodes
		
		SubscriptionType subscriptionType = (SubscriptionType)binding;
		
		// - DELIVER-INFO (0..*)
		List<DeliveryInfoType> deliveryInfos = subscriptionType.getDeliveryInfo();
		
		// - ENDTIME (0..1)
		XMLGregorianCalendar endtime = subscriptionType.getEndTime();
		
		// - NOTIFICATION-INTERVAL (0..1)
		Duration notificationInterval = subscriptionType.getNotificationInterval();
		
		// - SELECTOR (1..1)
		QueryType selector = subscriptionType.getSelector();
		
		// - STARTTIME (0..1)
		XMLGregorianCalendar starttime = subscriptionType.getStartTime();
		
		// ===== FILL NODE =====

		// - DELIVER-INFO (0..*)
		if (deliveryInfos.isEmpty() == false) {
			
			for (DeliveryInfoType deliveryInfo:deliveryInfos) {

				Node deliveryInfoTypeNode = DeliveryInfoTypeNEO.toNode(graphDB, deliveryInfo, checkReference);
				node.createRelationshipTo(deliveryInfoTypeNode, RelationTypes.hasDeliveryInfo);

			}

		}

		// - ENDTIME (0..1)
		if (endtime != null) node.setProperty(OASIS_RIM_ENDTIME, endtime);

		// - NOTIFICATION-INTERVAL (0..1)
		if (notificationInterval != null) node.setProperty(OASIS_RIM_NOTIFICATION_INTERVAL, notificationInterval);

		// - SELECTOR (1..1)
		Node queryTypeNode = QueryTypeNEO.toNode(graphDB, selector, checkReference);		
		node.createRelationshipTo(queryTypeNode, RelationTypes.hasSelector);

		// - STARTTIME (0..1)
		
		// in case of no starttime specified, the server uses the current time
		// to indicate the starttime of this subscription
		if (starttime == null) starttime = CalendarUtil.toXMLGregorianCalendar(new Date());
		node.setProperty(OASIS_RIM_STARTTIME, starttime);

		return node;
		
	}

	public static Object toBinding(Node node) {
		return toBinding(node, null);
	}

	public static Object toBinding(Node node, String language) {
		
		SubscriptionType binding = factory.createSubscriptionType();
		binding = (SubscriptionType)RegistryObjectTypeNEO.fillBinding(node, binding, language);

		Iterable<Relationship> relationships = null;

		// - DELIVER-INFO (0..*)
		relationships = node.getRelationships(RelationTypes.hasDeliveryInfo);
		if (relationships != null) {
			
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node deliveryInfoTypeNode = relationship.getEndNode();
				
				DeliveryInfoType deliveryInfoType = (DeliveryInfoType)DeliveryInfoTypeNEO.toBinding(deliveryInfoTypeNode);				
				binding.getDeliveryInfo().add(deliveryInfoType);

			}
			
		}

		// - ENDTIME (0..1)
		if (node.hasProperty(OASIS_RIM_ENDTIME)) binding.setEndTime((XMLGregorianCalendar)node.getProperty(OASIS_RIM_ENDTIME));

		// - NOTIFICATION-INTERVAL (0..1)
		if (node.hasProperty(OASIS_RIM_NOTIFICATION_INTERVAL)) binding.setNotificationInterval((Duration)node.getProperty(OASIS_RIM_NOTIFICATION_INTERVAL));

		// - SELECTOR (1..1)
		relationships = node.getRelationships(RelationTypes.hasSelector);
		if (relationships != null) {
			
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node queryTypeNode = relationship.getEndNode();
				
				QueryType queryType = (QueryType)QueryTypeNEO.toBinding(queryTypeNode);				
				binding.setSelector(queryType);

			}
			
		}

		// - STARTTIME (0..1)
		if (node.hasProperty(OASIS_RIM_STARTTIME)) binding.setStartTime((XMLGregorianCalendar)node.getProperty(OASIS_RIM_STARTTIME));
		
		return binding;
		
	}
	
	public static String getNType() {
		return "SubscriptionType";
	}
}
