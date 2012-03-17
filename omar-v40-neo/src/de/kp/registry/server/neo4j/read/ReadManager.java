package de.kp.registry.server.neo4j.read;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.query.QueryResponse;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;
import org.oasis.ebxml.registry.bindings.rim.QueryType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;

import de.kp.registry.server.neo4j.database.Database;
import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.exception.InvalidRequestException;
import de.kp.registry.server.neo4j.spi.CanonicalConstants;
import de.kp.registry.server.neo4j.spi.QueryRequestContext;

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

	public List<ObjectRefType> getObjectRefsByQuery(QueryType query) {
		return null;
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
 	
 	public QueryResponse executeQuery(QueryRequestContext context, QueryResponse response) { 		
 		
 		// retrieve cypher query language statement from the query context
 		try {
 			
 			String cypherQuery = context.getCypherQuery();
 			if (cypherQuery == null) throw new InvalidRequestException("[QueryRequest] The query expression associated with the request is invalid.");
 			 
 			// reflect the incoming parameter 'startIndex'
 			response.setStartIndex(context.getStartIndex());

 			// the name of the request row is uniquely described as "n"
 	 		ExecutionResult result = engine.execute(cypherQuery);
 	 		Iterator<Node> nodes = result.columnAs("n");

 	 		// the result depends on the response option and the 
 	 		// return type defined there
 	 		String returnType = context.getReturnType();
 	 		if (returnType.equals(CanonicalConstants.LEAF_CLASS)) {
 	 			
 	 			// This option specifies that the QueryResponse MUST contain a collection of
 	 			// <rim:RegistryObjectList> element containing <rim:RegistryObject> elements 
 	 			// that have an xsi:type attribute that corresponds to leaf classes as defined 
 	 			// in [regrep-xsd-v4.0]. No RepositoryItems SHOULD be included for any 
 	 			// rim:ExtrinsicObjectType instance in the <rim:Registry-ObjectList> element.
 	 			addLeafClassToResponse(context, nodes, response);
 	 		
 	 		} else if (returnType.equals(CanonicalConstants.LEAF_CLASS_RI)) {
 
 	 			// This option is the same as the LeafClass option with the additional
 	 			// requirement that the response include the RepositoryItems, if any, 
 	 			// for every rim:ExtrinsicObjectType instance in the <rim:RegistryObjectList> 
 	 			// element.
 	 			addLeafClassRIToResponse(context, nodes, response);
 	 		
 	 		} else if (returnType.equals(CanonicalConstants.OBJECT_REF)) {

 	 			// This option specifies that the QueryResponse MUST contain a 
 	 			// <rim:ObjectRefList> element. The purpose of this option is to 
 	 			// return references to objects rather than the actual objects.
 	 			addObjectRefToResponse(context, nodes, response);
 	 			
 	 		} else if (returnType.equals(CanonicalConstants.REGISTRY_OBEJCT)) {

 	 			// This option specifies that the QueryResponse MUST contain a 
 	 			// <rim:RegistryObjectList> element containing <rim:RegistryObject> 
 	 			// elements with xsi:type=“rim:RegistryObjectType”.
 	 			addRegistryObjectToResponse(context, nodes, response);
 	 			
 	 		}
 			
 		} catch (Exception e) {
 		
 			// TODO
 			
 			// in case of an exception, we fill the respective exception 
 			// into the queryResponse
 			
 		}
 		
 		return response; 		
 	
 	}

 	private void addLeafClassToResponse(QueryRequestContext context, Iterator<Node> nodes, QueryResponse response) {

		if (response.getRegistryObjectList() == null) response.setRegistryObjectList(ebRIMFactory.createRegistryObjectListType());

		// the language provided is used to evaluate internal strings
		String language = context.getLanguage();
		
		int totalResultCount = 0;
		try {
	 		while (nodes.hasNext()) {
	 			
	 			// increment total result
	 			totalResultCount += 1;
	 			
	 			Node node = nodes.next();
	 			RegistryObjectType binding = (RegistryObjectType)toBinding(node, language);

	 			// TODO
	 			
	 			response.getRegistryObjectList().getRegistryObject().add(binding);
	
	 		}

		} catch (Exception e) {
			
			// TODO
		}
		
 		// add total result count to response
 		response.setTotalResultCount(new BigInteger(String.valueOf(totalResultCount)));
 			
 	}

 	private void addLeafClassRIToResponse(QueryRequestContext context, Iterator<Node> nodes, QueryResponse response) {

		if (response.getRegistryObjectList() == null) response.setRegistryObjectList(ebRIMFactory.createRegistryObjectListType());

		// the language provided is used to evaluate internal strings
		String language = context.getLanguage();

		int totalResultCount = 0;		
		try {

			while (nodes.hasNext()) {
	 			
	 			// increment total result
	 			totalResultCount += 1;
	 			
	 			Node node = nodes.next();
	 			RegistryObjectType binding = (RegistryObjectType)toBinding(node, language);

	 			// TODO
	 			
	 			response.getRegistryObjectList().getRegistryObject().add(binding);

	 		}
			
		} catch (Exception e) {
			
			// TODO
			
		}

 		// add total result count to response
 		response.setTotalResultCount(new BigInteger(String.valueOf(totalResultCount)));
 			
 	}

 	private void addObjectRefToResponse(QueryRequestContext context, Iterator<Node> nodes, QueryResponse response) {

		if (response.getObjectRefList() == null) response.setObjectRefList(ebRIMFactory.createObjectRefListType());

		int totalResultCount = 0;
 		while (nodes.hasNext()) {
 			
 			// increment total result
 			totalResultCount += 1;
 			
 			Node node = nodes.next();
 			String id = (String)node.getProperty(NEOBase.OASIS_RIM_ID);

 			ObjectRefType objectRef = ebRIMFactory.createObjectRefType();
 			objectRef.setId(id);
 			
 			response.getObjectRefList().getObjectRef().add(objectRef);

 		}

 		// add total result count to response
 		response.setTotalResultCount(new BigInteger(String.valueOf(totalResultCount)));
 		
 	}
 	
 	private void addRegistryObjectToResponse(QueryRequestContext context, Iterator<Node> nodes, QueryResponse response) {

		if (response.getRegistryObjectList() == null) response.setRegistryObjectList(ebRIMFactory.createRegistryObjectListType());

		// the language provided is used to evaluate internal strings
		String language = context.getLanguage();

		int totalResultCount = 0;		
		try {
			
	 		while (nodes.hasNext()) {
	 			
	 			// increment total result
	 			totalResultCount += 1;
	 			
	 			Node node = nodes.next();
	 			RegistryObjectType binding = (RegistryObjectType)toBinding(node, language);
	 			
	 			response.getRegistryObjectList().getRegistryObject().add(binding);
	
	 		}

		} catch(Exception e) {
		
			// TODO
		}
 		
		// add total result count to response
 		response.setTotalResultCount(new BigInteger(String.valueOf(totalResultCount)));
 			
 	}

}
