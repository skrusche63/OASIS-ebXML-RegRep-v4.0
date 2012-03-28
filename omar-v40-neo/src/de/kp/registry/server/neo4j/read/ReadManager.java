package de.kp.registry.server.neo4j.read;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.AnyValueType;
import org.oasis.ebxml.registry.bindings.rim.BooleanValueType;
import org.oasis.ebxml.registry.bindings.rim.CollectionValueType;
import org.oasis.ebxml.registry.bindings.rim.DateTimeValueType;
import org.oasis.ebxml.registry.bindings.rim.DurationValueType;
import org.oasis.ebxml.registry.bindings.rim.FloatValueType;
import org.oasis.ebxml.registry.bindings.rim.IntegerValueType;
import org.oasis.ebxml.registry.bindings.rim.MapValueType;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;
import org.oasis.ebxml.registry.bindings.rim.QueryDefinitionType;
import org.oasis.ebxml.registry.bindings.rim.QueryType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;
import org.oasis.ebxml.registry.bindings.rim.SlotType;
import org.oasis.ebxml.registry.bindings.rim.StringQueryExpressionType;
import org.oasis.ebxml.registry.bindings.rim.StringValueType;
import org.oasis.ebxml.registry.bindings.rim.ValueType;
import org.oasis.ebxml.registry.bindings.rim.VocabularyTermValueType;

import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.server.neo4j.database.Database;
import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.exception.InvalidRequestException;
import de.kp.registry.server.neo4j.service.context.QueryRequestContext;
import de.kp.registry.server.neo4j.service.context.QueryResponseContext;

public class ReadManager {

	private static ReadManager instance = new ReadManager();
	
	// reference to the Cipher execution engine
	ExecutionEngine engine;

	// reference to OASIS ebRIM object factory
	public static org.oasis.ebxml.registry.bindings.rim.ObjectFactory ebRIMFactory = new org.oasis.ebxml.registry.bindings.rim.ObjectFactory();
	
	private ReadManager() {
		
		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		engine = new ExecutionEngine(graphDB);
		
	}

	public static ReadManager getInstance() {
		if (instance == null) instance = new ReadManager();
		return instance;
	}

	// this method retrieves a list of object references from
	// a certain registered query definition (addressed by a
	// query type instance)
	
	public List<ObjectRefType> getObjectRefsByQuery(QueryType query) {
		
		List<ObjectRefType> objectRefs = null;
		
		try {

			String cypherQuery = getCypherQuery(query);
			if (cypherQuery == null) return objectRefs;
			
			objectRefs = new ArrayList<ObjectRefType>();
			Iterator<Node> nodes = executeCypherQuery(cypherQuery);
			while (nodes.hasNext()) {

				Node node = nodes.next();			
				String id = (String)node.getProperty(NEOBase.OASIS_RIM_ID);
				
				ObjectRefType objectRef = ebRIMFactory.createObjectRefType();
				objectRef.setId(id);
				
				objectRefs.add(objectRef);

			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectRefs;
	
	}
	
	// this is a common method to retrieve a certain node
	// from the index, identified by its 'id' property
	
	// note, that 'id' refers to the OASIS
 	public Node findNodeByID(String id) { 		
 		// the node index is built from the OASIS ebRIM 'id'
 		return Database.getInstance().getNodeIndex().get(NEOBase.OASIS_RIM_ID, id).getSingle();		
	}
 
 	public Object toBinding(Node node, String language) throws Exception {

		String rimClassName = (String) node.getProperty(NEOBase.NEO4J_TYPE);
		Class<?> clazz = NEOBase.getClassNEOByName(rimClassName);
		
		// call toBinding()
		Method method = clazz.getMethod("toBinding", Node.class, String.class);
		return method.invoke(null, node, language);

	}
 	
 	public Iterator<Node> executeCypherQuery(String cypherQuery) {

 		// the name of the request row is uniquely described as "n"
 		ExecutionResult result = engine.execute(cypherQuery);
	 	return result.columnAs("n");
 		
 	}
 	
 	public QueryResponseContext executeQuery(QueryRequestContext request, QueryResponseContext response) { 		
 		
 		// retrieve cypher query language statement from the query context
 		try {
 			
 			String cypherQuery = request.getCypherQuery();
 			if (cypherQuery == null) throw new InvalidRequestException("[QueryRequest] The query expression associated with the request is invalid.");
 			 
 			// reflect the incoming parameter 'startIndex'
 			response.setStartIndex(request.getStartIndex());

 			// the name of the request row is uniquely described as "n"
 	 		ExecutionResult result = engine.execute(cypherQuery);
 	 		Iterator<Node> nodes = result.columnAs("n");

 	 		// the result depends on the response option and the 
 	 		// return type defined there
 	 		String returnType = request.getReturnType();
 	 		if (returnType.equals(CanonicalConstants.LEAF_CLASS)) {
 	 			addLeafClassToResponse(request, nodes, response);
 	 		
 	 		} else if (returnType.equals(CanonicalConstants.LEAF_CLASS_RI)) {
 
 	 			// This option is the same as the LeafClass option with the additional
 	 			// requirement that the response include the RepositoryItems, if any, 
 	 			// for every rim:ExtrinsicObjectType instance in the <rim:RegistryObjectList> 
 	 			// element.
 	 			addLeafClassRIToResponse(request, nodes, response);
 	 		
 	 		} else if (returnType.equals(CanonicalConstants.OBJECT_REF)) {

 	 			// This option specifies that the QueryResponse MUST contain a 
 	 			// <rim:ObjectRefList> element. The purpose of this option is to 
 	 			// return references to objects rather than the actual objects.
 	 			addObjectRefToResponse(request, nodes, response);
 	 			
 	 		} else if (returnType.equals(CanonicalConstants.REGISTRY_OBEJCT)) {

 	 			// This option specifies that the QueryResponse MUST contain a 
 	 			// <rim:RegistryObjectList> element containing <rim:RegistryObject> 
 	 			// elements with xsi:type=“rim:RegistryObjectType”.
 	 			addRegistryObjectToResponse(request, nodes, response);
 	 			
 	 		}
 			
 		} catch (Exception e) {
 		
 			// this exception describes an InvalidQueryException
 			// due to an invalid query expression
 			response.addException(e);
 			
 		}
 		
 		return response; 		
 	
 	}

	// this helper method retrieves the cypher language statement
	// that represents a certain query request
	public String getCypherQuery(QueryType query) throws Exception {
		
		// Attribute queryDefinition – References the parameterized query 
		// to be invoked by the server. The value of this attribute MUST 
		// be a reference to a QueryDefinitionType instance that is supported
		// by the server.

		String queryDefinition = query.getQueryDefinition();
		
		// retrieve the referenced query definition type
		Node node = findNodeByID(queryDefinition);
		if (node == null) return null;
		
		// __DESIGN__
		
		// in order to process a query request, we use the respective 
		// binding of the QueryDefinitionType node
		
		QueryDefinitionType queryDefinitionType = (QueryDefinitionType)toBinding(node, null);		
		StringQueryExpressionType queryExpressionType = (StringQueryExpressionType)queryDefinitionType.getQueryExpression(); 

		if ("CYPHER".equals(queryExpressionType.getQueryLanguage()) == false) return null;
		String queryExpression = queryExpressionType.getValue();
		
		// Element Slot (Inherited) - Each Slot element specifies a parameter 
		// value for a parameter supported by the QueryDefinitionType instance.
		
		// * The slot name MUST match a parameterName attribute within a Parameter's 
		//   definition within the QueryDefinitionType instance.
		
		// * The slot value's type MUST match the dataType attribute for the Parameter's 
		// definition within the QueryDefinitionType instance.
		
		// * A server MUST NOT treat the order of parameters as significant.	
		
		List<SlotType> queryParameters = query.getSlot();
		if (queryParameters == null) return queryExpression;

		for (SlotType queryParameter:queryParameters) {

			// the parameter within a query expression is described by $parameterName
			String parameterName  = "$" + queryParameter.getName();
			String parameterValue = getParameterValue(queryParameter);
			
			if (parameterValue != null) queryExpression.replace(parameterName, parameterValue);
			
		}
		
		return queryExpression;
		
	}
	
	private String getParameterValue(SlotType parameter) {
		
		ValueType valueHolder = parameter.getSlotValue();
		if (valueHolder == null) return null;
		
		if (valueHolder instanceof StringValueType) {
			return ((StringValueType)valueHolder).getValue();						

		} else if (valueHolder instanceof DateTimeValueType) {
			
			XMLGregorianCalendar value = ((DateTimeValueType)valueHolder).getValue();
			return value.toString();
			
		} else if (valueHolder instanceof VocabularyTermValueType) {
			// NOT SUPPORTED
			
		} else if (valueHolder instanceof IntegerValueType) {
			
			BigInteger value = ((IntegerValueType)valueHolder).getValue();
			return value.toString();

		} else if (valueHolder instanceof AnyValueType) {
			// NOT SUPPORTED

		} else if (valueHolder instanceof BooleanValueType) {
			
			Boolean value = ((BooleanValueType)valueHolder).isValue();
			return new Boolean(value).toString();

		} else if (valueHolder instanceof FloatValueType) {
			
			Float value = ((FloatValueType)valueHolder).getValue();
			return Float.toString(value);
			
		} else if (valueHolder instanceof MapValueType) {
			// NOT SUPPORTED
			
		} else if (valueHolder instanceof DurationValueType) {
			
			Duration value = ((DurationValueType)valueHolder).getValue();
			return value.toString();

		} else if (valueHolder instanceof CollectionValueType) {
			// NOT SUPPORTED

		}
		
		return null;

	}
			
	// This option specifies that the QueryResponse MUST contain a collection of
	// <rim:RegistryObjectList> element containing <rim:RegistryObject> elements 
	// that have an xsi:type attribute that corresponds to leaf classes as defined 
	// in [regrep-xsd-v4.0]. No RepositoryItems SHOULD be included for any 
	// rim:ExtrinsicObjectType instance in the <rim:Registry-ObjectList> element.

 	// TODO: matchOlderVersions
 	
 	private void addLeafClassToResponse(QueryRequestContext request, Iterator<Node> nodes, QueryResponseContext response) {

		// the language provided is used to evaluate internal strings
		String language = request.getLanguage();
		
		int totalResultCount = 0;
		try {
	 		while (nodes.hasNext()) {
	 			
	 			// increment total result
	 			totalResultCount += 1;
	 			
	 			Node node = nodes.next();
	 			RegistryObjectType binding = (RegistryObjectType)toBinding(node, language);

	 			// TODO
	 			
	 			response.addRegistryObject(binding);
	
	 		}

		} catch (Exception e) {
			
			// add exception to response
			response.addException(e);
			
		}
		
 		// add total result count to response
 		response.setTotalResultCount(new BigInteger(String.valueOf(totalResultCount)));
 			
 	}

 	private void addLeafClassRIToResponse(QueryRequestContext request, Iterator<Node> nodes, QueryResponseContext response) {

		// the language provided is used to evaluate internal strings
		String language = request.getLanguage();

		int totalResultCount = 0;		
		try {

			while (nodes.hasNext()) {
	 			
	 			// increment total result
	 			totalResultCount += 1;
	 			
	 			Node node = nodes.next();
	 			RegistryObjectType binding = (RegistryObjectType)toBinding(node, language);

	 			// TODO
	 				 			
	 			response.addRegistryObject(binding);

	 		}
			
		} catch (Exception e) {
			
			// add exception to response
			response.addException(e);
			
		}

 		// add total result count to response
 		response.setTotalResultCount(new BigInteger(String.valueOf(totalResultCount)));
 			
 	}

 	private void addObjectRefToResponse(QueryRequestContext request, Iterator<Node> nodes, QueryResponseContext response) {

		int totalResultCount = 0;
 		while (nodes.hasNext()) {
 			
 			// increment total result
 			totalResultCount += 1;
 			
 			Node node = nodes.next();
 			String id = (String)node.getProperty(NEOBase.OASIS_RIM_ID);

 			ObjectRefType objectRef = ebRIMFactory.createObjectRefType();
 			objectRef.setId(id);
 			
 			response.addObjectRef(objectRef);

 		}

 		// add total result count to response
 		response.setTotalResultCount(new BigInteger(String.valueOf(totalResultCount)));
 		
 	}
 	
 	private void addRegistryObjectToResponse(QueryRequestContext request, Iterator<Node> nodes, QueryResponseContext response) {

		// the language provided is used to evaluate internal strings
		String language = request.getLanguage();

		int totalResultCount = 0;		
		try {
			
	 		while (nodes.hasNext()) {
	 			
	 			// increment total result
	 			totalResultCount += 1;
	 			
	 			Node node = nodes.next();
	 			RegistryObjectType binding = (RegistryObjectType)toBinding(node, language);
	 			
	 			response.addRegistryObject(binding);
	
	 		}

		} catch(Exception e) {
			
			// add exception to response
			response.addException(e);
		}
 		
		// add total result count to response
 		response.setTotalResultCount(new BigInteger(String.valueOf(totalResultCount)));
 			
 	}

}
