package de.kp.registry.server.neo4j.domain.event;

import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.AuditableEventType;
import org.oasis.ebxml.registry.bindings.rim.NotificationType;

import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.exception.UnresolvedReferenceException;
import de.kp.registry.server.neo4j.read.ReadManager;

public class NotificationTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new NotificationType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		// create node from underlying RegistryObjectType
		Node node = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a NotificationType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);

	}

	// this method replaces an existing NotificationType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier

	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return fillNode(graphDB, node, binding, checkReference, false);
	}

	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference, boolean excludeVersion) throws RegistryException {
		
		// clear NotificationType specific parameters
		node = clearNode(node, excludeVersion);
		
		// clear & fill node with RegistryObjectType specific parameters
		node = RegistryObjectTypeNEO.fillNode(graphDB, node, binding, checkReference, excludeVersion);
		
		// fill node with NotificationType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 

	}

	public static Node clearNode(Node node, boolean excludeVersion) {
		
		// - EVENT (1..*)
		
		// __DESIGN__
		
		// An AuditableEventType node is NOT an intrinsic information
		// that us ultimately related with a NotificationType node
		
		// clear relationship only
		node = NEOBase.clearRelationship(node, RelationTypes.hasAuditableEvent, false);
		
		// - SUBSCRIPTION 1..1)
		node.removeProperty(OASIS_RIM_SUBSCRIPTION);

		return node;
		
	}

	// this is a common wrapper to delete a NotificationType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear NotificationType specific parameters
		node = clearNode(node, false);
		
		// clear node fromRegistryObjectType specific parameters and remove
		RegistryObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);

	}
	
	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		NotificationType notificationType = (NotificationType)binding;
		
		// - EVENT (1..*)
		List<AuditableEventType> events = notificationType.getEvent();
		
		// - SUBSCRIPTION 1..1)
		String subscription = notificationType.getSubscription();
		
		// ===== FILL NODE =====

		// - EVENT (1..*)
		for (AuditableEventType event:events) {

			Node auditableEventTypeNode = null;
			if (checkReference == true) {

				// we have to make sure that the referenced AuditableEventType
				// references an existing node in the database

				String nid = event.getId();					
				auditableEventTypeNode = ReadManager.getInstance().findNodeByID(nid);
				
				if (auditableEventTypeNode == null) 
					throw new UnresolvedReferenceException("[NotificationType] AuditableEventType node with id '" + nid + "' does not exist.");		
			
			} else {
				auditableEventTypeNode = AuditableEventTypeNEO.toNode(graphDB, event, checkReference);
			}
			
			node.createRelationshipTo(auditableEventTypeNode, RelationTypes.hasAuditableEvent);

		}
		
		// - SUBSCRIPTION 1..1)
		if (checkReference == true) {

			// make sure that the SubscriptionType references an existing node within the database
			if (ReadManager.getInstance().findNodeByID(subscription) == null) 
				throw new UnresolvedReferenceException("[NotificationType] SubscriptionType node with id '" + subscription + "' does not exist.");		
			
		}
		
		node.setProperty(OASIS_RIM_SUBSCRIPTION, subscription);

		return node;

	}
	
	public static Object toBinding(Node node) {
		return toBinding(node, null);
	}

	public static Object toBinding(Node node, String language) {
		
		NotificationType binding = factory.createNotificationType();
		binding = (NotificationType)RegistryObjectTypeNEO.fillBinding(node, binding, language);

		// - EVENT (1..*)
		Iterable<Relationship> relationships = node.getRelationships(RelationTypes.hasAuditableEvent);
		if (relationships != null) {
			
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node auditableEventTypeNode = relationship.getEndNode();
				
				AuditableEventType auditableEventType = (AuditableEventType)AuditableEventTypeNEO.toBinding(auditableEventTypeNode);				
				binding.getEvent().add(auditableEventType);

			}
			
		}

		// - SUBSCRIPTION 1..1)
		binding.setSubscription((String)node.getProperty(OASIS_RIM_SUBSCRIPTION));

		return binding;
		
	}
	
	public static String getNType() {
		return "NotificationType";
	}
}
