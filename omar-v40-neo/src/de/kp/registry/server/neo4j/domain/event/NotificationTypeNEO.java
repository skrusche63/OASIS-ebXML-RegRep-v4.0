package de.kp.registry.server.neo4j.domain.event;

import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.AuditableEventType;
import org.oasis.ebxml.registry.bindings.rim.NotificationType;

import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;

public class NotificationTypeNEO extends RegistryObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		NotificationType notificationType = (NotificationType)binding;
		
		// - EVENT (1..*)
		List<AuditableEventType> events = notificationType.getEvent();
		
		// - SUBSCRIPTION 1..1)
		String subscription = notificationType.getSubscription();
		
		// create node from underlying RegistryObjectType
		Node notificationTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a NotificationType
		notificationTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - EVENT (1..*)
		for (AuditableEventType event:events) {

			Node auditableEventTypeNode = AuditableEventTypeNEO.toNode(graphDB, event);
			notificationTypeNode.createRelationshipTo(auditableEventTypeNode, RelationTypes.hasAuditableEvent);

		}
		
		// - SUBSCRIPTION 1..1)
		notificationTypeNode.setProperty(OASIS_RIM_SUBSCRIPTION, subscription);

		return notificationTypeNode;
	}

	public static Object toBinding(Node node) {
		
		NotificationType binding = factory.createNotificationType();
		return binding;
		
	}
	
	public static String getNType() {
		return "NotificationType";
	}
}
