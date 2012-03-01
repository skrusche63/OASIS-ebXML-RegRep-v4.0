package de.kp.registry.server.neo4j.domain.event;

import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ActionType;
import org.oasis.ebxml.registry.bindings.rim.AuditableEventType;
import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;

public class AuditableEventTypeNEO extends RegistryObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		AuditableEventType auditableEventType = (AuditableEventType)binding;
		
		// - ACTION (1..*)
		List<ActionType> actions = auditableEventType.getAction();
		
		// - REQUEST-ID (1..1)
		String requestId = auditableEventType.getRequestId();
		
		// - TIMESTAMP (1..1)
		XMLGregorianCalendar timestamp = auditableEventType.getTimestamp();
		
		// - USER (1..1)
		String user = auditableEventType.getUser();

		// create node from underlying RegistryObjectType
		Node auditableEventTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe an AuditableEventType
		auditableEventTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - ACTION (1..*)
		for (ActionType action:actions) {

			Node actionTypeNode = ActionTypeNEO.toNode(graphDB, action);
			auditableEventTypeNode.createRelationshipTo(actionTypeNode, RelationTypes.hasAction);

		}
		
		// - REQUEST-ID (1..1)
		auditableEventTypeNode.setProperty(OASIS_RIM_REQUEST_ID, requestId);

		// - TIMESTAMP (1..1)
		auditableEventTypeNode.setProperty(OASIS_RIM_TIMESTAMP, timestamp);

		// - USER (1..1)
		auditableEventTypeNode.setProperty(OASIS_RIM_USER, user);
		
		return auditableEventTypeNode;

	}

	public static Object toBinding(Node node) {
		
		AuditableEventType binding = factory.createAuditableEventType();
		binding = (AuditableEventType)RegistryObjectTypeNEO.fillBinding(node, binding);

		// - ACTION (1..*)
		Iterable<Relationship> relationships = node.getRelationships(RelationTypes.hasAction);
		if (relationships != null) {
			
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node actionTypeNode = relationship.getEndNode();
				
				ActionType actionType = (ActionType)ActionTypeNEO.toBinding(actionTypeNode);				
				binding.getAction().add(actionType);

			}

		}

		// - REQUEST-ID (1..1)
		binding.setRequestId((String)node.getProperty(OASIS_RIM_REQUEST_ID));
		
		// - TIMESTAMP (1..1)
		binding.setTimestamp((XMLGregorianCalendar)node.getProperty(OASIS_RIM_TIMESTAMP));
		
		// - USER (1..1)
		binding.setUser((String)node.getProperty(OASIS_RIM_USER));
		
		return binding;
		
	}
	public static String getNType() {
		return "AuditableEventType";
	}
}
