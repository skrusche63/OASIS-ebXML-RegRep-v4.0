package de.kp.registry.server.neo4j.domain.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ExtensibleObjectType;
import org.oasis.ebxml.registry.bindings.rim.SlotType;

import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.RelationTypes;

// starting from the OASIS ebRIM v4.0 information model, this is
// the basic class for all other data structures

public class ExtensibleObjectTypeNEO extends NEOBase {

	// this method creates a new ExtensibleObjectType node within database;
	// note, that we do not have to check references to other registry
	// objects, provided with this ExtensibleObjectType
	
	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		// build extensibleObjectType node
		Node node = graphDB.createNode();
		
		// add internal administration properties
		node.setProperty(NEO4J_UID, getNID());		
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding);
		
	}

	// this method replaces an existing ExtensibleObjectType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier

	// note, that we do not have to check references to other registry
	// objects, provided with this ExtensibleObjectType
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding) throws Exception {
		
		node = clearNode(node);
		return fillNodeInternal(graphDB, node, binding); 

	}
	
	public static Node clearNode(Node node) {
		
		// - SLOTS (0..*)
		Iterable<Relationship> relationships = node.getRelationships(RelationTypes.hasSlot);
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
					((Node)removable).delete();
				
				else if (removable instanceof Relationship)
					((Relationship)removable).delete();
			}
			
		}

		return node;
		
	}
	
	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding) throws Exception {

		ExtensibleObjectType extensibleObjectType = (ExtensibleObjectType)binding;
		
		// - SLOTS (0..*)
		List<SlotType> slots = extensibleObjectType.getSlot();		
		if (slots.isEmpty()) return node;

		// ===== FILL NODE =====

		// - SLOTS (0..*)
		for (SlotType slot:slots) {
			
			// create a SlotType node and associate the extensibleObjectType node
			// via the relationship 'hasSlot'
			Node slotTypeNode = SlotTypeNEO.toNode(graphDB, slot);
			node.createRelationshipTo(slotTypeNode, RelationTypes.hasSlot);
			
		}

		return node;
		
	}
	
	// this method supports query requests and provides the ExtensibleObjectType
	// of the overall JAXB binding
	
	public static Object fillBinding(Node node, Object binding) {
		
		ExtensibleObjectType extensibleObjectType = (ExtensibleObjectType)binding;
		
		// - SLOTS (0..*)
		Iterable<Relationship> relationships = node.getRelationships(RelationTypes.hasSlot);
		if (relationships != null) {
		
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node slotTypeNode = relationship.getEndNode();
				
				SlotType slotType = (SlotType)SlotTypeNEO.toBinding(slotTypeNode);				
				extensibleObjectType.getSlot().add(slotType);

			}
			
		}

		return extensibleObjectType;
		
	}
	
	public static String getNType() {
		return "ExtensibleObjectType";
	}
}
