package de.kp.registry.server.neo4j.write;

import java.math.BigInteger;
import java.util.List;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.jxpath.JXPathContext;
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
	
	private static UpdateProcessor instance = new UpdateProcessor();
	
	private UpdateProcessor() {}
	
	public static UpdateProcessor getInstance() {
		if (instance == null) instance = new UpdateProcessor();
		return instance;
	}
	
	// each UpdateObjectsRequest defines a specific update action 
	// to be performed on each target object
	public Object updateBinding(Object binding, Boolean checkReference, List<UpdateActionType> updateActions) throws RegistryException {
		
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

			// update binding object with specific value
			binding = updateBinding(binding, xpath, valueHolder, checkReference, mode);
			
		}
		
		return binding;
		
	}
	
	private Object updateBinding(Object binding, String selector, ValueType valueHolder, Boolean checkReference, String mode) throws RegistryException {
		
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

			// String is a single value instance either of
			// a certain registry object or a specific slot
			
			String newValue = ((StringValueType)valueHolder).getValue();						
			String oldValue = (String)updateContext.getValue(selector);

			updateSingleValue(updateContext, selector, checkReference, mode, oldValue, newValue);

		} else if (valueHolder instanceof DateTimeValueType) {

			// DateTime is a single value instance either of
			// a certain registry object or a specific slot

			XMLGregorianCalendar newValue = ((DateTimeValueType)valueHolder).getValue();
			XMLGregorianCalendar oldValue = (XMLGregorianCalendar)updateContext.getValue(selector);

			updateSingleValue(updateContext, selector, checkReference, mode, oldValue, newValue);

		} else if (valueHolder instanceof InternationalStringValueType) {

			// InternationalString is a single value instance of a
			// certain registry object; it is NOT supported for slots

			InternationalStringType newValue = ((InternationalStringValueType)valueHolder).getValue();
			InternationalStringType oldValue = (InternationalStringType)updateContext.getValue(selector);

			updateSingleValue(updateContext, selector, checkReference, mode, oldValue, newValue);
			
		} else if (valueHolder instanceof VocabularyTermValueType) {

			// VocabularyTerm is a single value instance of a
			// certain slot; it is NOT supported for registry
			// objects

			VocabularyTermType newValue = ((VocabularyTermValueType)valueHolder).getValue();
			VocabularyTermType oldValue = (VocabularyTermType)updateContext.getValue(selector);

			updateSingleValue(updateContext, selector, checkReference, mode, oldValue, newValue);
			
		} else if (valueHolder instanceof IntegerValueType) {

			// Integer is a single value instance either of
			// a certain registry object or a specific slot

			BigInteger newValue = ((IntegerValueType)valueHolder).getValue();
			BigInteger oldValue = (BigInteger)updateContext.getValue(selector);

			updateSingleValue(updateContext, selector, checkReference, mode, oldValue, newValue);

		} else if (valueHolder instanceof AnyValueType) {

			// Any (Object) is a single value instance of a
			// certain slot; it is NOT supported for registry
			// objects

			Object newValue = ((AnyValueType)valueHolder).getAny();
			Object oldValue = (Object)updateContext.getValue(selector);

			updateSingleValue(updateContext, selector, checkReference, mode, oldValue, newValue);

		} else if (valueHolder instanceof BooleanValueType) {

			// Boolean is a single value instance either of
			// a certain registry object or a specific slot

			Boolean newValue = ((BooleanValueType)valueHolder).isValue();
			Boolean oldValue = (Boolean)updateContext.getValue(selector);

			updateSingleValue(updateContext, selector, checkReference, mode, oldValue, newValue);

		} else if (valueHolder instanceof FloatValueType) {
			
			// Float is a single value instance either of
			// a certain registry object or a specific slot
			
			Float newValue = ((FloatValueType)valueHolder).getValue();
			Float oldValue = (Float)updateContext.getValue(selector);

			updateSingleValue(updateContext, selector, checkReference, mode, oldValue, newValue);
			
		} else if (valueHolder instanceof MapValueType) {

			// Map is a single value instance of a certain
			// slot; it is NOT supported for registry objects

			MapType newValue = ((MapValueType)valueHolder).getMap();
			MapType oldValue = (MapType)updateContext.getValue(selector);

			updateSingleValue(updateContext, selector, checkReference, mode, oldValue, newValue);

		} else if (valueHolder instanceof SlotValueType) {

			// SlotType is a single value instance of a certain
			// registry object; it is NOT supported for slots
			
			SlotType newValue = ((SlotValueType)valueHolder).getSlot();
			@SuppressWarnings("unchecked")
			List<SlotType> oldValue = (List<SlotType>)updateContext.getValue(selector);

			updateSlotValue(updateContext, selector, checkReference, mode, oldValue, newValue);

		} else if (valueHolder instanceof DurationValueType) {

			// Duration is a single value instance either of
			// a certain registry object or a specific slot

			Duration newValue = ((DurationValueType)valueHolder).getValue();
			Duration oldValue = (Duration)updateContext.getValue(selector);

			updateSingleValue(updateContext, selector, checkReference, mode, oldValue, newValue);

		} else if (valueHolder instanceof CollectionValueType) {

			// Collection is a single value instance of a certain 
			// slot; it is NOT supported for registry object

			// for processing, please refer to SlotTypeNEO
			CollectionValueType newValue = (CollectionValueType)valueHolder;
			CollectionValueType oldValue = (CollectionValueType)updateContext.getValue(selector);

			updateSingleValue(updateContext, selector, checkReference, mode, oldValue, newValue);

		}
		
		binding = updateContext.getContextBean();
		return binding;
		
	}

	private void updateSingleValue(JXPathContext updateContext, String selector, Boolean checkReference, String mode, Object oldValue, Object newValue) throws RegistryException {
		
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
	
	private void updateSlotValue(JXPathContext updateContext, String selector, Boolean checkReference, String mode, List<SlotType> oldValue, SlotType newValue) throws RegistryException {

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
			
		} else if (mode.equals("Delete")) {
			
			// Indicates that the node identified by selector MUST be deleted from 
			// the target object if it is present.

		}

	}
}
