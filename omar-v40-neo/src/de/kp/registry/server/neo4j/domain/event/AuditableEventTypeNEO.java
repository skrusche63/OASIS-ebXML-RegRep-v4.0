package de.kp.registry.server.neo4j.domain.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ActionType;
import org.oasis.ebxml.registry.bindings.rim.AuditableEventType;

import de.kp.registry.server.neo4j.database.ReadManager;
import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.exception.UnresolvedReferenceException;

public class AuditableEventTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new AuditableType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {

		// create node from underlying RegistryObjectType
		Node node = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe an AuditableEventType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);

	}

	// this method replaces an existing AuditableEventType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier

	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return fillNode(graphDB, node, binding, checkReference, false);
	}

	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference, boolean excludeVersion) throws RegistryException {
		
		// clear AuditableEventType specific parameters
		node = clearNode(node, excludeVersion);
		
		// clear & fill node with RegistryObjectType specific parameters
		node = RegistryObjectTypeNEO.fillNode(graphDB, node, binding, checkReference, excludeVersion);
		
		// fill node with AuditableEventType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 
	}
	
	public static Node clearNode(Node node, boolean excludeVersion) {
		
		// - ACTION (1..*)
		
		// __DESIGN__
		
		// ActionType nodes are an intrinsic part of an AuditableEventType
		// and are therefore removed in addition to the respective relationships

		// clear relationship and referenced ActionType nodes (cascading removal)
		String deletionScope = "";
		
		boolean checkReference = false;
		boolean deleteChildren = false;
		
		node = clearActions(node, checkReference, deleteChildren, deletionScope);
		
		// - REQUEST-ID (1..1)
		node.removeProperty(OASIS_RIM_REQUEST_ID);

		// - TIMESTAMP (1..1)
		node.removeProperty(OASIS_RIM_TIMESTAMP);

		// - USER (1..1)
		node.removeProperty(OASIS_RIM_USER);
		
		return node;
		
	}

	// this is a common wrapper to delete an AuditableEventType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear AuditableEventType specific parameters
		node = clearNode(node, false);
		
		// clear node fromRegistryObjectType specific parameters and remove
		RegistryObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);

	}
	
	// __CASCADING REMOVAL__
	
	// this method is part of the cascading delete strategy for AuditableEventType nodes
	
	private static Node clearActions(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		Iterable<Relationship> relationships = node.getRelationships(RelationTypes.hasAction);
		if (relationships != null) {

			List<Object>removables = new ArrayList<Object>();

			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
				
				Relationship relationship = iterator.next();
				removables.add(relationship);
				
				Node endNode = relationship.getEndNode();
				removables.add(endNode);

			}

			// remove all collected node and relationships
			while (removables.size() > 0) {
				
				Object removable = removables.get(0);
				if (removable instanceof Node)
					// this is a dedicated removal of an ActionType node
					ActionTypeNEO.removeNode((Node)removable, checkReference, deleteChildren, deletionScope);
				
				else if (removable instanceof Relationship)
					((Relationship)removable).delete();
			}

		}

		return node;
		
	}

	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		AuditableEventType auditableEventType = (AuditableEventType)binding;
		
		// - ACTION (1..*)
		List<ActionType> actions = auditableEventType.getAction();
		
		// - REQUEST-ID (1..1)
		String requestId = auditableEventType.getRequestId();
		
		// - TIMESTAMP (1..1)
		XMLGregorianCalendar timestamp = auditableEventType.getTimestamp();
		
		// - USER (1..1)
		String user = auditableEventType.getUser();

		// ===== FILL NODE =====

		// - ACTION (1..*)
		for (ActionType action:actions) {

			// An ActionType is an ExtensibleObjectType without any referenceable
			// unique identifier; the parameter 'checkReferenec' must not be
			// evaluated in this context
			Node actionTypeNode = ActionTypeNEO.toNode(graphDB, action, checkReference);
			node.createRelationshipTo(actionTypeNode, RelationTypes.hasAction);

		}
		
		// - REQUEST-ID (1..1)
		node.setProperty(OASIS_RIM_REQUEST_ID, requestId);

		// - TIMESTAMP (1..1)
		node.setProperty(OASIS_RIM_TIMESTAMP, timestamp);

		// - USER (1..1)
		if (checkReference == true) {

			// make sure that the PersonType node references an existing node within the database
			if (ReadManager.getInstance().findNodeByID(user) == null) 
				throw new UnresolvedReferenceException("[AuditableEventType] PersonType node with id '" + user + "' does not exist.");		

		}
		
		node.setProperty(OASIS_RIM_USER, user);
		
		return node;

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
