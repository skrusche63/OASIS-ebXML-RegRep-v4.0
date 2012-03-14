package de.kp.registry.server.neo4j.domain.core;

import java.math.BigInteger;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.AnyValueType;
import org.oasis.ebxml.registry.bindings.rim.BooleanValueType;
import org.oasis.ebxml.registry.bindings.rim.CollectionValueType;
import org.oasis.ebxml.registry.bindings.rim.DateTimeValueType;
import org.oasis.ebxml.registry.bindings.rim.DurationValueType;
import org.oasis.ebxml.registry.bindings.rim.FloatValueType;
import org.oasis.ebxml.registry.bindings.rim.IntegerValueType;
import org.oasis.ebxml.registry.bindings.rim.MapType;
import org.oasis.ebxml.registry.bindings.rim.MapValueType;
import org.oasis.ebxml.registry.bindings.rim.SlotType;
import org.oasis.ebxml.registry.bindings.rim.StringValueType;
import org.oasis.ebxml.registry.bindings.rim.ValueType;
import org.oasis.ebxml.registry.bindings.rim.VocabularyTermValueType;
import org.oasis.ebxml.registry.bindings.rim.VocabularyTermType;

import de.kp.registry.server.neo4j.database.ReadManager;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.exception.UnresolvedReferenceException;

// __DESIGN__

// SlotType values are provided by a value holder; 
// in order to synchronize slot processing with the
// update handling of registry objects, we have to
// extract the values from the value holder

public class SlotTypeNEO extends ExtensibleObjectTypeNEO {

	// a SlotType is also an ExtensibleObjectType and may have SlotTypes
	// assigned; these secondary slotTypes are actually not supported
	
	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws RegistryException {
		
		SlotType slotType = (SlotType)binding;
		
		// - NAME (1..1)
		String slotTypeName = slotType.getName();
		
		// - TYPE (0..1)
		String slotTypeType = slotType.getType();
		
		// this secondary slot is actually not supported
		// List<SlotType> slots = slotType.getSlot();
		
		// - VALUE (0..1)
		Object slotTypeValu = extractValue(slotType);
		
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
	
	public static Object toBinding(Node node) {
		
		SlotType binding = factory.createSlotType();

		// - NAME (1..1)
		binding.setName((String)node.getProperty(OASIS_RIM_SLOT_NAME));

		// - TYPE (0..1)
		if (node.hasProperty(OASIS_RIM_SLOT_TYPE)) binding.setType((String)node.getProperty(OASIS_RIM_SLOT_TYPE));
		
		// - VALUE (0..1)
		if (node.hasProperty(OASIS_RIM_SLOT_VALU)) binding.setSlotValue(buildValueHolder(node));
		
		return binding;

	}
	
	private static Object extractValue(SlotType slot) throws RegistryException {
		
		// the following ValueTypes are not supported as SlotType values:
		//
		// - InternationalStringValueType
		// - SlotValueType
		
		ValueType valueHolder = slot.getSlotValue();
		if (valueHolder == null) return null;
		
		if (valueHolder instanceof StringValueType) {
			return ((StringValueType)valueHolder).getValue();						

		} else if (valueHolder instanceof DateTimeValueType) {
			return ((DateTimeValueType)valueHolder).getValue();
			
		} else if (valueHolder instanceof VocabularyTermValueType) {
			return ((VocabularyTermValueType)valueHolder).getValue();
			
		} else if (valueHolder instanceof IntegerValueType) {
			return ((IntegerValueType)valueHolder).getValue();

		} else if (valueHolder instanceof AnyValueType) {
			return ((AnyValueType)valueHolder).getAny();

		} else if (valueHolder instanceof BooleanValueType) {
			return ((BooleanValueType)valueHolder).isValue();

		} else if (valueHolder instanceof FloatValueType) {
			return ((FloatValueType)valueHolder).getValue();
			
		} else if (valueHolder instanceof MapValueType) {
			return ((MapValueType)valueHolder).getMap();
			
		} else if (valueHolder instanceof DurationValueType) {
			return ((DurationValueType)valueHolder).getValue();

		} else if (valueHolder instanceof CollectionValueType) {

			String collectionType = ((CollectionValueType)valueHolder).getCollectionType();

			if (ReadManager.getInstance().findNodeByID(collectionType) == null) 
				throw new UnresolvedReferenceException("[SlotType] ClassificationNodeType node with id '" + collectionType + "' does not exist.");		
			
			return (CollectionValueType)valueHolder;

		}
		
		return null;
		
	}

	private static ValueType buildValueHolder(Node node) {
		
		// the following ValueTypes are not supported as SlotType values:
		//
		// - InternationalStringValueType
		// - SlotValueType
		
		Object value = node.getProperty(OASIS_RIM_SLOT_VALU);
		if (value instanceof String) {
			
			StringValueType valueHolder = factory.createStringValueType();
			valueHolder.setValue((String)value);
			
			return valueHolder;

		} else if (value instanceof XMLGregorianCalendar) {

			DateTimeValueType valueHolder = factory.createDateTimeValueType();
			valueHolder.setValue((XMLGregorianCalendar)value);

			return valueHolder;
			
		} else if (value instanceof VocabularyTermType) {

			VocabularyTermValueType valueHolder = factory.createVocabularyTermValueType();
			valueHolder.setValue((VocabularyTermType)value);

			return valueHolder;
			
		} else if (value instanceof BigInteger) {

			IntegerValueType valueHolder = factory.createIntegerValueType();
			valueHolder.setValue((BigInteger)value);

			return valueHolder;
			
		} else if (value instanceof Object) {

			AnyValueType valueHolder = factory.createAnyValueType();
			valueHolder.setAny((Object)value);

			return valueHolder;
			
		} else if (value instanceof Boolean) {

			BooleanValueType valueHolder = factory.createBooleanValueType();
			valueHolder.setValue((Boolean)value);

		} else if (value instanceof Float) {
			
			FloatValueType valueHolder = factory.createFloatValueType();
			valueHolder.setValue((Float)value);

			return valueHolder;
			
		} else if (value instanceof MapType) {

			MapValueType valueHolder = factory.createMapValueType();
			valueHolder.setMap((MapType)value);

			return valueHolder;
			
		} else if (value instanceof Duration) {

			DurationValueType valueHolder = factory.createDurationValueType();
			valueHolder.setValue((Duration)value);

			return valueHolder;

		} else if (value instanceof CollectionValueType) {

			// in case of a collection of ValueTypes, the
			// respective value holder is registered as
			// slot value; this is due to the fact, that
			// the 
			return (CollectionValueType)value;

		}
		
		// unknown value type
		return null;
		
	}

	public static String getNType() {
		return "SlotType";
	}
	
}
