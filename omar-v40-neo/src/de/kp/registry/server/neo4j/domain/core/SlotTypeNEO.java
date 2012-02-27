package de.kp.registry.server.neo4j.domain.core;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.AnyValueType;
import org.oasis.ebxml.registry.bindings.rim.SlotType;


public class SlotTypeNEO extends ExtensibleObjectTypeNEO {

	// a SlotType is also an ExtensibleObjectType and may have SlotTypes
	// assigned; these secondary slotTypes are actually not supported
	
	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) {
		
		SlotType slotType = (SlotType)binding;
		
		// - NAME (1..1)
		String slotTypeName = slotType.getName();
		
		// - TYPE (0..1)
		String slotTypeType = slotType.getType();
		
		// this secondary slot is actually not supported
		// List<SlotType> slots = slotType.getSlot();
		
		// the value of a slotType is actually restricted to anyType,
		// which is just an object representation
		
		// - VALUE (0..1)
		Object slotTypeValu = null;
		AnyValueType value = (AnyValueType)slotType.getSlotValue();
		
		if (value != null) slotTypeValu = value.getAny();
		
		// build slotType node
		Node slotTypeNode = graphDB.createNode();
		
		// add internal administration properties
		slotTypeNode.setProperty(NEO4J_UID, getNID());		
		slotTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - NAME (1..1)
		slotTypeNode.setProperty(OASIS_RIM_SLOT_NAME, slotTypeName);		

		// - TYPE (0..1)
		if (slotTypeType != null) slotTypeNode.setProperty(OASIS_RIM_SLOT_TYPE, slotTypeType);
		
		// - VALUE (0..1)
		if (slotTypeValu != null) slotTypeNode.setProperty(OASIS_RIM_SLOT_VALU, slotTypeValu);
		
		return slotTypeNode;
		
	}
	
	public static String getNType() {
		return "SlotType";
	}
	
}
