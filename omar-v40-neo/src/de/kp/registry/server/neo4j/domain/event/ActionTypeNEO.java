package de.kp.registry.server.neo4j.domain.event;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ActionType;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefListType;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectListType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;

import de.kp.registry.server.neo4j.database.ReadManager;
import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.exception.UnresolvedReferenceException;

/*
 * __DESIGN__ 
 * 
 * An ActionType instance refers to either EXISTING RegistryObjectType instances
 * or EXISTING ObjectRefType instance; if not such instances are found, no node
 * will be created for the respective binding object
 */

public class ActionTypeNEO extends ExtensibleObjectTypeNEO {

	// this method creates a new ActionType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		// create node from underlying ExtensibleObjectType
		Node node = ExtensibleObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe an ActionType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);

	}

	public static Node clearNode(Node node, boolean excludeVersion) {

		// - AFFECTED-OBJECT (0..1)

		// clear relationship only
		node = NEOBase.clearRelationship(node, RelationTypes.hasAffectedObject, false);

		// - AFFECTED-OBJECT-REF (0..1)

		// clear relationship only
		node = NEOBase.clearRelationship(node, RelationTypes.hasAffectedObjectRef, false);

		// - EVENT-TYPE (1..1)
		node.removeProperty(OASIS_RIM_EVENT_TYPE);
		
		return node;
		
	}

	// this is a common wrapper to delete an ActionType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear ActionType specific parameters
		node = clearNode(node, false);
		
		// clear node from ExtensibleObjectType specific parameters and remove
		ExtensibleObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}

	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		// __DESIGN__
		
		// the parameter 'checkReference' is not evaluated for ActionType as the 
		// references affect object (refs) MUST exist in the database
		
		ActionType actionType = (ActionType)binding;
		
		// - AFFECTED-OBJECT (0..1)
		RegistryObjectListType affectedObjects = actionType.getAffectedObjects();
		
		// - AFFECTED-OBJECT-REF (0..1)
		ObjectRefListType affectedObjectRefs = actionType.getAffectedObjectRefs();
		
		// - EVENT-TYPE (1..1)
		String eventType = actionType.getEventType();
				
		// ===== FILL NODE =====
		
		ReadManager rm = ReadManager.getInstance();

		// - AFFECTED-OBJECT (0..1)
		if (affectedObjects != null) {
			
			// the list of registry objects associated with a certain
			// ActionType instance refers to already existing registry
			// objects
			
			List<RegistryObjectType> registryObjects = affectedObjects.getRegistryObject();
			for (RegistryObjectType registryObject:registryObjects) {
				
				String id = registryObject.getId();
				Node registryObjectTypeNode = rm.findNodeByID(id);
				
				if (registryObjectTypeNode == null) throw new UnresolvedReferenceException("[ActionType] Affected object node for '" + id + "' does not exist");
				node.createRelationshipTo(registryObjectTypeNode, RelationTypes.hasAffectedObject);

			}
			
		}
		
		// - AFFECTED-OBJECT-REF (0..1)
		if (affectedObjectRefs != null) {

			// the list of object refs associated with a certain
			// ActionType instance refers to already existing object refs

			List<ObjectRefType> objectRefs = affectedObjectRefs.getObjectRef();
			for (ObjectRefType objectRef:objectRefs) {

				String id = objectRef.getId();
				Node objectRefTypeNode = rm.findNodeByID(id);

				if (objectRefTypeNode == null) throw new UnresolvedReferenceException("[ActionType] Affected object node for '" + id + "' does not exist");
				node.createRelationshipTo(objectRefTypeNode, RelationTypes.hasAffectedObjectRef);

			}

		}
		
		// - EVENT-TYPE (1..1)
		if (checkReference == true) {
			
			// make sure that the classification node references an existing node within the database
			if (ReadManager.getInstance().findNodeByID(eventType) == null) 
				throw new UnresolvedReferenceException("[ActionType] Classification node with id '" + eventType + "' does not exist.");		


		}
		
		node.setProperty(OASIS_RIM_EVENT_TYPE, eventType);
		
		return node;

	}

	public static Object toBinding(Node node) {
	
		ActionType binding = factory.createActionType();
		binding = (ActionType)ExtensibleObjectTypeNEO.fillBinding(node, binding);

		Iterable<Relationship> relationships = null;
		
		// - AFFECTED-OBJECT (0..1)
		relationships = node.getRelationships(RelationTypes.hasAffectedObject);
		if (relationships != null) {
			
			RegistryObjectListType registryObjectTypeList = factory.createRegistryObjectListType();

			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node registryObjectTypeNode = relationship.getEndNode();

				try {

					Class<?> clazz = getClassNEO(registryObjectTypeNode.getProperty(NEO4J_TYPE));
					Method method = clazz.getMethod("toBinding", Node.class);
					
					RegistryObjectType registryObjectType = (RegistryObjectType)method.invoke(null, registryObjectTypeNode);
					registryObjectTypeList.getRegistryObject().add(registryObjectType);
					
				} catch (Exception e) {
					e.printStackTrace();
					
				}

			}

			binding.setAffectedObjects(registryObjectTypeList);

		}

		// - AFFECTED-OBJECT-REF (0..1)
		relationships = node.getRelationships(RelationTypes.hasAffectedObjectRef);
		if (relationships != null) {
			
			ObjectRefListType objectRefTypeList = factory.createObjectRefListType();

			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node objectRefTypeNode = relationship.getEndNode();

				try {

					Class<?> clazz = getClassNEO(objectRefTypeNode.getProperty(NEO4J_TYPE));
					Method method = clazz.getMethod("toBinding", Node.class);
					
					ObjectRefType objectRefType = (ObjectRefType)method.invoke(null, objectRefTypeNode);
					objectRefTypeList.getObjectRef().add(objectRefType);
					
				} catch (Exception e) {
					e.printStackTrace();
					
				}

			}

			binding.setAffectedObjectRefs(objectRefTypeList);

		}

		// - EVENT-TYPE (1..1)
		binding.setEventType((String)node.getProperty(OASIS_RIM_EVENT_TYPE));
		
		return binding;
		
	}
	
	public static String getNType() {
		return "ActionType";
	}
}
