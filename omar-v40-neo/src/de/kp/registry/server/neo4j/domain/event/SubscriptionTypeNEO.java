package de.kp.registry.server.neo4j.domain.event;

import java.util.List;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.DeliveryInfoType;
import org.oasis.ebxml.registry.bindings.rim.QueryType;
import org.oasis.ebxml.registry.bindings.rim.SubscriptionType;

import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.query.QueryTypeNEO;

public class SubscriptionTypeNEO extends RegistryObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
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

		// create node from underlying RegistryObjectType
		Node subscriptionTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a SubscriptionType
		subscriptionTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - DELIVER-INFO (0..*)
		if (deliveryInfos.isEmpty() == false) {
			
			for (DeliveryInfoType deliveryInfo:deliveryInfos) {

				Node deliveryInfoTypeNode = DeliveryInfoTypeNEO.toNode(graphDB, deliveryInfo);
				subscriptionTypeNode.createRelationshipTo(deliveryInfoTypeNode, RelationTypes.hasDeliveryInfo);

			}

		}

		// - ENDTIME (0..1)
		if (endtime != null) subscriptionTypeNode.setProperty(OASIS_RIM_ENDTIME, endtime);

		// - NOTIFICATION-INTERVAL (0..1)
		if (notificationInterval != null) subscriptionTypeNode.setProperty(OASIS_RIM_NOTIFICATION_INTERVAL, notificationInterval);

		// - SELECTOR (1..1)
		Node queryTypeNode = QueryTypeNEO.toNode(graphDB, selector);
		subscriptionTypeNode.createRelationshipTo(queryTypeNode, RelationTypes.hasSelector);

		// - STARTTIME (0..1)
		if (starttime != null) subscriptionTypeNode.setProperty(OASIS_RIM_STARTTIME, starttime);

		return subscriptionTypeNode;
	}

	public static Object toBinding(Node node) {
		
		SubscriptionType binding = factory.createSubscriptionType();
		return binding;
		
	}
	
	public static String getNType() {
		return "SubscriptionType";
	}
}
