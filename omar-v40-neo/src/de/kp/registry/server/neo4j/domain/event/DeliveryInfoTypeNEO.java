package de.kp.registry.server.neo4j.domain.event;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.DeliveryInfoType;
import org.w3._2005._08.addressing.EndpointReferenceType;

import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;

/*
 * __DESIGN__ 
 * 
 * The EndpointReferenceType (NOTIFY-TO) is assigned as a property for
 * a DeliveryInfoType node
 * 
 */
public class DeliveryInfoTypeNEO extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		DeliveryInfoType deliveryInfoType = (DeliveryInfoType)binding;
		
		// - NOTIFICATION-OPTION (0..1)
		String notificationOption = deliveryInfoType.getNotificationOption();
		
		// - NOTIFY-TO (1..1)
		EndpointReferenceType endpointReference = deliveryInfoType.getNotifyTo();

		// create node from underlying ExtensibleObjectType
		Node deliveryInfoTypeNode = ExtensibleObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a  DeliveryInfoType
		deliveryInfoTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - NOTIFICATION-OPTION (0..1)
		if (notificationOption != null) deliveryInfoTypeNode.setProperty(OASIS_RIM_NOTIFICATION_OPTION, notificationOption);

		// - NOTIFY-TO (1..1)
		deliveryInfoTypeNode.setProperty(OASIS_RIM_NOTIFY_TO, endpointReference);
		
		return deliveryInfoTypeNode;
		
	}

	public static Object toBinding(Node node) {
	
		DeliveryInfoType binding = factory.createDeliveryInfoType();
		return binding;
		
	}
	
	public static String getNType() {
		return "DeliveryInfoType";
	}
}
