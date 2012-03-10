package de.kp.registry.server.neo4j.domain.core;

import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ExtensibleObjectType;
import org.oasis.ebxml.registry.bindings.rim.SlotType;

import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

// starting from the OASIS ebRIM v4.0 information model, this is
// the basic class for all other data structures

public class ExtensibleObjectTypeNEO extends NEOBase {

	// this method creates a new ExtensibleObjectType node within database;
	// note, that we do not have to check references to other registry
	// objects, provided with this ExtensibleObjectType
	
	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		// build extensibleObjectType node
		Node node = graphDB.createNode();
		
		// add internal administration properties
		node.setProperty(NEO4J_UID, getNID());		
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);
		
	}

	// this method replaces an existing ExtensibleObjectType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier

	// note, that we do not have to check references to other registry
	// objects, provided with this ExtensibleObjectType
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		
		node = clearNode(node, false);
		return fillNodeInternal(graphDB, node, binding, checkReference); 

	}
	
	public static Node clearNode(Node node, boolean excludeVersion) {
		
		// - SLOTS (0..*)

		// clear relationship and referenced SlotType nodes
		node = NEOBase.clearRelationship(node, RelationTypes.hasSlot, true);

		return node;
		
	}

	// this is a common wrapper to delete an ExtensibleObjectType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear ExtensibleType specific parameters
		node = clearNode(node, false);
		node.delete();
		
	}

	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

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
