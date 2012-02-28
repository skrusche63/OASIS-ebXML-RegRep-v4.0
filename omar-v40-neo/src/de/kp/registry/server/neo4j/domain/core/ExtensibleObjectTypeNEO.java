package de.kp.registry.server.neo4j.domain.core;

import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ExtensibleObjectType;
import org.oasis.ebxml.registry.bindings.rim.SlotType;

import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.RelationTypes;

// starting from the OASIS ebRIM v4.0 information model, this is
// the basic class for all other data structures

public class ExtensibleObjectTypeNEO extends NEOBase {

	// this method creates a node and respective relationships
	// to potential slots; it is embedded (due to transaction
	// handling) into another superior method
	
	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
				
		ExtensibleObjectType extensibleObjectType = (ExtensibleObjectType)binding;
		
		// - SLOTS (0..*)
		List<SlotType> slots = extensibleObjectType.getSlot();
		
		// build extensibleObjectType node
		Node extensibleObjectTypeNode = graphDB.createNode();
		
		// add internal administration properties
		extensibleObjectTypeNode.setProperty(NEO4J_UID, getNID());		
		extensibleObjectTypeNode.setProperty(NEO4J_TYPE, getNType());
		
		// - SLOTS (0..*)
		if (slots.isEmpty()) return extensibleObjectTypeNode;
		
		for (SlotType slot:slots) {
			
			// create a SlotType node and associate the extensibleObjectType node
			// via the relationship 'hasSlot'
			Node slotTypeNode = SlotTypeNEO.toNode(graphDB, slot);
			extensibleObjectTypeNode.createRelationshipTo(slotTypeNode, RelationTypes.hasSlot);
			
		}
		
		return extensibleObjectTypeNode;
		
	}
	
	public static Node updateNode(EmbeddedGraphDatabase graphDB, Object binding) {
		return null;
	}

	public static boolean deleteNode(EmbeddedGraphDatabase graphDB, Object binding) {
		return false;
	}
	
	public static Object fillBinding(Node node, Object binding) {
		
		// get slots from node
		return binding;
		
	}
	
	public static String getNType() {
		return "ExtensibleObjectType";
	}
}
