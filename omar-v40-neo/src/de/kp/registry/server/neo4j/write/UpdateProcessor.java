package de.kp.registry.server.neo4j.write;

import java.math.BigInteger;
import java.util.List;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.jxpath.JXPathContext;
import org.neo4j.graphdb.Node;
import org.oasis.ebxml.registry.bindings.lcm.UpdateActionType;
import org.oasis.ebxml.registry.bindings.rim.AnyValueType;
import org.oasis.ebxml.registry.bindings.rim.BooleanValueType;
import org.oasis.ebxml.registry.bindings.rim.CollectionValueType;
import org.oasis.ebxml.registry.bindings.rim.DateTimeValueType;
import org.oasis.ebxml.registry.bindings.rim.DurationValueType;
import org.oasis.ebxml.registry.bindings.rim.FloatValueType;
import org.oasis.ebxml.registry.bindings.rim.IntegerValueType;
import org.oasis.ebxml.registry.bindings.rim.InternationalStringType;
import org.oasis.ebxml.registry.bindings.rim.InternationalStringValueType;
import org.oasis.ebxml.registry.bindings.rim.MapType;
import org.oasis.ebxml.registry.bindings.rim.MapValueType;
import org.oasis.ebxml.registry.bindings.rim.QueryExpressionType;
import org.oasis.ebxml.registry.bindings.rim.SlotType;
import org.oasis.ebxml.registry.bindings.rim.SlotValueType;
import org.oasis.ebxml.registry.bindings.rim.StringQueryExpressionType;
import org.oasis.ebxml.registry.bindings.rim.StringValueType;
import org.oasis.ebxml.registry.bindings.rim.ValueType;
import org.oasis.ebxml.registry.bindings.rim.VocabularyTermType;
import org.oasis.ebxml.registry.bindings.rim.VocabularyTermValueType;
import org.oasis.ebxml.registry.bindings.rim.XMLQueryExpressionType;

import de.kp.registry.server.neo4j.domain.exception.InvalidRequestException;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class UpdateProcessor {

	public UpdateProcessor() {}
	
	// each UpdateObjectsRequest defines a specific update action 
	// to be performed on each target object
	public void process(Node node, Object binding, Boolean checkReference, List<UpdateActionType> updateActions) throws RegistryException {
		
		for (UpdateActionType updateAction:updateActions) {
			
			// - SELECTOR
			
			// A QueryExpressionType that contains the expression that identifies 
			// a node of the resource representation to be updated
			
			// Actually there is only support for XPATH expressions
			QueryExpressionType selector = updateAction.getSelector();
			if (selector instanceof StringQueryExpressionType)
				throw new InvalidRequestException("[UpdateObjectsRequest] Invalid QueryExpressionType provided.");
				
			// retrieve xpath expression from selector
			String xpath = (String) ((XMLQueryExpressionType)selector).getAny();
			
			// - VALUE HOLDER
			
			// This element contains the value to be written to the target object. 
			// If the mode attribute is "Insert" or "Update" then this element MUST
			// be present. If the mode is "Delete" then this element MUST NOT be present
			ValueType valueHolder = updateAction.getValueHolder();
			
			// this is an abstract value type and must be converted into a specific one.
			
			// - MODE			
			String mode = updateAction.getMode();

			// update node from updated binding object with specific value
			updateNode(node, binding, xpath, valueHolder, mode);
			
		}
		
	}
	
	private void updateNode(Node node, Object binding, String selector, ValueType valueHolder, String mode) throws RegistryException {
		
		// retrieve xpath context from binding
		JXPathContext updateContext = JXPathContext.newContext(binding);		

		// retrieve value from valueHolder
		
		// __DESIGN__
		
		// in the actual version of the UpdateProcessor, we except that the
		// data type of the value provided (by the value holder) is equal to
		// the data type of the target that is retrieved from the xpath
		
		// due to this restriction, the update of list-based & map-based
		// slot values is actually not supported
				
		if (valueHolder instanceof StringValueType) {

			// single value instance
			String newValue = ((StringValueType)valueHolder).getValue();						
			String oldValue = (String)updateContext.getValue(selector);

			updateSingleValue(node, updateContext, selector, mode, oldValue, newValue);

		} else if (valueHolder instanceof DateTimeValueType) {

			// single value instance
			XMLGregorianCalendar newValue = ((DateTimeValueType)valueHolder).getValue();
			XMLGregorianCalendar oldValue = (XMLGregorianCalendar)updateContext.getValue(selector);

			updateSingleValue(node, updateContext, selector, mode, oldValue, newValue);

		} else if (valueHolder instanceof InternationalStringValueType) {

			// single value instance
			InternationalStringType newValue = ((InternationalStringValueType)valueHolder).getValue();
			InternationalStringType oldValue = (InternationalStringType)updateContext.getValue(selector);

			updateSingleValue(node, updateContext, selector, mode, oldValue, newValue);
			
		} else if (valueHolder instanceof VocabularyTermValueType) {

			// single value instance (SlotType only)
			VocabularyTermType newValue = ((VocabularyTermValueType)valueHolder).getValue();
			VocabularyTermType oldValue = (VocabularyTermType)updateContext.getValue(selector);

			updateSingleValue(node, updateContext, selector, mode, oldValue, newValue);
			
		} else if (valueHolder instanceof IntegerValueType) {

			// single value instance
			BigInteger newValue = ((IntegerValueType)valueHolder).getValue();
			BigInteger oldValue = (BigInteger)updateContext.getValue(selector);

			updateSingleValue(node, updateContext, selector, mode, oldValue, newValue);

		} else if (valueHolder instanceof AnyValueType) {

			// single value instance
			Object newValue = ((AnyValueType)valueHolder).getAny();
			Object oldValue = (Object)updateContext.getValue(selector);

			updateSingleValue(node, updateContext, selector, mode, oldValue, newValue);

		} else if (valueHolder instanceof BooleanValueType) {

			// single value instance
			Boolean newValue = ((BooleanValueType)valueHolder).isValue();
			Boolean oldValue = (Boolean)updateContext.getValue(selector);

			updateSingleValue(node, updateContext, selector, mode, oldValue, newValue);

		} else if (valueHolder instanceof FloatValueType) {
			
			// single value instance
			Float newValue = ((FloatValueType)valueHolder).getValue();
			Float oldValue = (Float)updateContext.getValue(selector);

			updateSingleValue(node, updateContext, selector, mode, oldValue, newValue);
			
		} else if (valueHolder instanceof MapValueType) {

			// single value instance (SlotType only)
			MapType newValue = ((MapValueType)valueHolder).getMap();
			MapType oldValue = (MapType)updateContext.getValue(selector);

			updateSingleValue(node, updateContext, selector, mode, oldValue, newValue);

		} else if (valueHolder instanceof SlotValueType) {

			// TODO: REVIEW APPROACH
			
			// multiple value instance
			SlotType newValue = ((SlotValueType)valueHolder).getSlot();
			List<SlotType> oldValue = (List<SlotType>)updateContext.getValue(selector);

			updateSlotValue(node, updateContext, selector, mode, oldValue, newValue);

		} else if (valueHolder instanceof DurationValueType) {

			// single value instance
			Duration newValue = ((DurationValueType)valueHolder).getValue();
			Duration oldValue = (Duration)updateContext.getValue(selector);

			updateSingleValue(node, updateContext, selector, mode,oldValue, newValue);

		} else if (valueHolder instanceof CollectionValueType) {

			// TODO
			
			// multiple value instance

			// this is restricted to the update of a certain slot value
			List<ValueType> value = ((CollectionValueType)valueHolder).getElement();
			String collectionType = ((CollectionValueType)valueHolder).getCollectionType();

		}
		
	}

	
	// FRAGE: muss man das update ähnlich kaskadierend aufbauen, wie das create, 
	// d.h. muss ich wissen, welches object betroffen ist?
	
	// ebenso unklar: wie wirkt sich der mode auf den node aus? kann man den node
	// gegebenenfalls direkt aktualisieren?

	private void updateSingleValue(Node node, JXPathContext updateContext, String selector, String mode, Object oldValue, Object newValue) throws RegistryException {

		// evaluate mode of request
		if (mode.equals("Insert")) {

			// Indicates that the value provided by ValueHolder MUST be added to the target object.
			//
			// If the selector targets a repeated element (maxOccurs > 1), the node MUST be 
			// added at the end. If the selector targets a non-repeated element (maxOccurrs = 1) 
			// that already exists, the resource MUST generate an InvalidRequestException with a 
			// fault detail of NodeAlreadyExistsException.
			//
			// If the selector targets an existing item of a repeated element, the value provided
			// by ValueHolder MUST be added before the existing item.

		} else if (mode.equals("Update")) {

			// Indicates that the node identified by selector MUST be replaced by 
			// value by the ValueHolder in its place. If the selector resolves to
			// nothing then there should be no change to the target object.

			if (oldValue != null) updateContext.setValue(selector, newValue);
			
		} else if (mode.equals("Delete")) {
			
			// Indicates that the node identified by selector MUST be deleted from 
			// the target object if it is present.
			
			if (oldValue != null) updateContext.setValue(selector, null);

		}
		
	}
	
	private void updateSlotValue(Node node, JXPathContext updateContext, String selector, String mode, Object oldValue, Object newValue) throws RegistryException {
		
	}
}
