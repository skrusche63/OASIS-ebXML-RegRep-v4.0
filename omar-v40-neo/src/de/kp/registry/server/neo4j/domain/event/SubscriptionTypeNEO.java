package de.kp.registry.server.neo4j.domain.event;

import java.util.List;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.DeliveryInfoType;
import org.oasis.ebxml.registry.bindings.rim.QueryType;
import org.oasis.ebxml.registry.bindings.rim.SubscriptionType;

import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;

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
		
		return null;
	}

	public static String getNType() {
		return "SubscriptionType";
	}
}
