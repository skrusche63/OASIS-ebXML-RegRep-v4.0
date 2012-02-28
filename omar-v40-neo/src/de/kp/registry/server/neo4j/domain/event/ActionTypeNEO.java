package de.kp.registry.server.neo4j.domain.event;

import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ActionType;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefListType;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectListType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;

import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;

public class ActionTypeNEO extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) {
		
		ActionType actionType = (ActionType)binding;
		
		// - AFFECTED-OBJECT (0..1)
		RegistryObjectListType affectedObjects = actionType.getAffectedObjects();
		
		// - AFFECTED-OBJECT-REF (0..1)
		ObjectRefListType objectRefs = actionType.getAffectedObjectRefs();
		
		// - EVENT-TYPE (1..1)
		String eventType = actionType.getEventType();
		
		// create node from underlying ExtensibleObjectType
		Node actionTypeNode = ExtensibleObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe an ActionType
		actionTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - AFFECTED-OBJECT (0..1)
		if (affectedObjects != null) {
			List<RegistryObjectType> registryObjects = affectedObjects.getRegistryObject();
			
			// TODO: müssen die Registry Objects bereits existieren
			
		}
		
		// - AFFECTED-OBJECT-REF (0..1)
		if (objectRefs != null) {
			List<ObjectRefType> objectRefTypes = objectRefs.getObjectRef();
			
			// TODO

		}
		
		// - EVENT-TYPE (1..1)
		actionTypeNode.setProperty(OASIS_RIM_EVENT_TYPE, eventType);
		
		return actionTypeNode;
	}

	public static String getNType() {
		return "ActionType";
	}
}
