package de.kp.registry.server.neo4j.domain.event;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.DeliveryInfoType;
import org.w3._2005._08.addressing.EndpointReferenceType;

import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

/*
 * __DESIGN__ 
 * 
 * The EndpointReferenceType (NOTIFY-TO) is assigned as a property for
 * a DeliveryInfoType node
 * 
 */
public class DeliveryInfoTypeNEO extends ExtensibleObjectTypeNEO {

	// this method creates a new DeliveryInfoType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {

		// create node from underlying ExtensibleObjectType
		Node node = ExtensibleObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a  DeliveryInfoType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);
		
	}

	public static Node clearNode(Node node, boolean excludeVersion) {

		// - NOTIFICATION-OPTION (0..1)
		if (node.hasProperty(OASIS_RIM_NOTIFICATION_OPTION)) node.removeProperty(OASIS_RIM_NOTIFICATION_OPTION);

		// - NOTIFY-TO (1..1)
		node.removeProperty(OASIS_RIM_NOTIFY_TO);
		
		return node;
		
	}

	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		// the parameter 'checkReference' is not evaluated for DeliveryInfoType nodes
		
		DeliveryInfoType deliveryInfoType = (DeliveryInfoType)binding;
		
		// - NOTIFICATION-OPTION (0..1)
		String notificationOption = deliveryInfoType.getNotificationOption();
		
		// - NOTIFY-TO (1..1)
		EndpointReferenceType endpointReference = deliveryInfoType.getNotifyTo();

		// ===== FILL NODE =====

		// - NOTIFICATION-OPTION (0..1)
		if (notificationOption != null) node.setProperty(OASIS_RIM_NOTIFICATION_OPTION, notificationOption);

		// - NOTIFY-TO (1..1)
		node.setProperty(OASIS_RIM_NOTIFY_TO, endpointReference);
		
		return node;

	}

	public static Object toBinding(Node node) {
	
		DeliveryInfoType binding = factory.createDeliveryInfoType();
		binding = (DeliveryInfoType)ExtensibleObjectTypeNEO.fillBinding(node, binding);

		// - NOTIFICATION-OPTION (0..1)
		if (node.hasProperty(OASIS_RIM_NOTIFICATION_OPTION)) binding.setNotificationOption((String)node.getProperty(OASIS_RIM_NOTIFICATION_OPTION));
		
		// - NOTIFY-TO (1..1)
		binding.setNotifyTo((EndpointReferenceType)node.getProperty(OASIS_RIM_NOTIFY_TO));
		
		return binding;
		
	}
	
	public static String getNType() {
		return "DeliveryInfoType";
	}
}
