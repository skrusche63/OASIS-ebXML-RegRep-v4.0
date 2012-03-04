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
import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

/*
 * __DESIGN__ 
 * 
 * An ActionType instance refers to either EXISTING RegistryObjectType instances
 * or EXISTING ObjectRefType instance; if not such instances are found, no node
 * will be created for the respective binding object
 */

public class ActionTypeNEO extends ExtensibleObjectTypeNEO {

	// this method creates a new ActionType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws RegistryException {
		
		ActionType actionType = (ActionType)binding;
		
		// - AFFECTED-OBJECT (0..1)
		RegistryObjectListType affectedObjects = actionType.getAffectedObjects();
		
		// - AFFECTED-OBJECT-REF (0..1)
		ObjectRefListType affectedObjectRefs = actionType.getAffectedObjectRefs();
		
		// - EVENT-TYPE (1..1)
		String eventType = actionType.getEventType();
		
		// create node from underlying ExtensibleObjectType
		Node actionTypeNode = ExtensibleObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe an ActionType
		actionTypeNode.setProperty(NEO4J_TYPE, getNType());

		// retrieve CipherQueryManager to determine already existing
		// nodes by their respective OASIS ebRIM id
		
		ReadManager cqm = ReadManager.getInstance();

		// - AFFECTED-OBJECT (0..1)
		if (affectedObjects != null) {
			
			// the list of registry objects associated with a certain
			// ActionType instance refers to already existing registry
			// objects
			
			List<RegistryObjectType> registryObjects = affectedObjects.getRegistryObject();
			for (RegistryObjectType registryObject:registryObjects) {
				
				String id = registryObject.getId();
				Node registryObjectTypeNode = cqm.findNodeByID(id);
				
				if (registryObjectTypeNode == null) throw new RegistryException("[AffectedObjects] No node found for id: " + id);
				actionTypeNode.createRelationshipTo(registryObjectTypeNode, RelationTypes.hasAffectedObject);

			}
			
		}
		
		// - AFFECTED-OBJECT-REF (0..1)
		if (affectedObjectRefs != null) {

			// the list of object refs associated with a certain
			// ActionType instance refers to already existing object refs

			List<ObjectRefType> objectRefs = affectedObjectRefs.getObjectRef();
			for (ObjectRefType objectRef:objectRefs) {

				String id = objectRef.getId();
				Node objectRefTypeNode = cqm.findNodeByID(id);

				if (objectRefTypeNode == null) throw new RegistryException("[AffectedObjectRefs] No node found for id: " + id);
				actionTypeNode.createRelationshipTo(objectRefTypeNode, RelationTypes.hasAffectedObjectRef);

			}

		}
		
		// - EVENT-TYPE (1..1)
		actionTypeNode.setProperty(OASIS_RIM_EVENT_TYPE, eventType);
		
		return actionTypeNode;
	}

	public static Node clearNode(Node node) {
		return null;
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
