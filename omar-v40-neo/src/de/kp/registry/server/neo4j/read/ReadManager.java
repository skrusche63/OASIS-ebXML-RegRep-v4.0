package de.kp.registry.server.neo4j.read;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.AnyValueType;
import org.oasis.ebxml.registry.bindings.rim.BooleanValueType;
import org.oasis.ebxml.registry.bindings.rim.ClassificationNodeType;
import org.oasis.ebxml.registry.bindings.rim.ClassificationSchemeType;
import org.oasis.ebxml.registry.bindings.rim.CollectionValueType;
import org.oasis.ebxml.registry.bindings.rim.CommentType;
import org.oasis.ebxml.registry.bindings.rim.DateTimeValueType;
import org.oasis.ebxml.registry.bindings.rim.DurationValueType;
import org.oasis.ebxml.registry.bindings.rim.ExtrinsicObjectType;
import org.oasis.ebxml.registry.bindings.rim.FederationType;
import org.oasis.ebxml.registry.bindings.rim.FloatValueType;
import org.oasis.ebxml.registry.bindings.rim.IntegerValueType;
import org.oasis.ebxml.registry.bindings.rim.MapValueType;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;
import org.oasis.ebxml.registry.bindings.rim.OrganizationType;
import org.oasis.ebxml.registry.bindings.rim.QueryDefinitionType;
import org.oasis.ebxml.registry.bindings.rim.QueryType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;
import org.oasis.ebxml.registry.bindings.rim.RegistryPackageType;
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
import de.kp.registry.server.neo4j.write.VersionHandler;

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
 
 	public Object getRegistryObject(String id) {

 		Node node = findNodeByID(id);
 		if (node == null) return null;
 		
 		Object binding = null; 		
 		try {
 	 		String language = null;
			binding = toBinding(node, language);
		
 		} catch (Exception e) {
			e.printStackTrace();
		}
 		
 		return binding;
 		
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

 	public Iterator<Node> executeCypherQuery(QueryType query) {

		try {

			String cypherQuery = getCypherQuery(query);

			// the name of the request row is uniquely described as "n"
	 		ExecutionResult result = engine.execute(cypherQuery);
		 	return result.columnAs("n");
		
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
 		
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

 	 		if (request.getMatchOlderVersions() == false) nodes = getLatestVersions(nodes);
 	 		
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

		if (CanonicalConstants.CYPHER_LANGUAGE.equals(queryExpressionType.getQueryLanguage()) == false) return null;
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

	 			// make sure that only leaf classes are added
	 			// to the query response
	 			if (isLeafClass(binding) == false) continue;
	 			
	 			// No RepositoryItems SHOULD be included for any rim:ExtrinsicObjectType 
	 			// instance in the <rim:Registry-ObjectList> element.	 			

	 			if ((binding instanceof CommentType) || (binding instanceof ExtrinsicObjectType)) {
	 				
	 				ExtrinsicObjectType eo = (ExtrinsicObjectType)binding;
	 				
	 				DataHandler repositoryItem = null;
	 				eo.setRepositoryItem(repositoryItem);
	 				
	 			}
	 			
 				response.addRegistryObject(binding);
	
	 		}

		} catch (Exception e) {
			
			// add exception to response
			response.addException(e);
			
		}
		
 		// add total result count to response
 		response.setTotalResultCount(new BigInteger(String.valueOf(totalResultCount)));
 			
 	}

	// This option is the same as the LeafClass option with the additional
	// requirement that the response include the RepositoryItems, if any, 
	// for every rim:ExtrinsicObjectType instance in the <rim:RegistryObjectList> 
	// element.

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

	 			// make sure that only leaf classes are added
	 			// to the query response
	 			if (isLeafClass(binding) == false) continue;
	 				 			
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

 	private boolean isLeafClass(RegistryObjectType registryObject) {
 		
 		boolean isLeaf = true;
 		if (registryObject instanceof ClassificationNodeType) {
 			
 			ClassificationNodeType cn = (ClassificationNodeType)registryObject;
 			return (cn.getClassificationNode().size() == 0) ? true : false;
 
 		} else if (registryObject instanceof ClassificationSchemeType)
 			return false;
 
 		else if (registryObject instanceof FederationType)
 			return false;

 		else if (registryObject instanceof OrganizationType) {
 			
 			OrganizationType org = (OrganizationType)registryObject;
 			return (org.getOrganization().size() == 0) ? true : false;
 			
 		} else if (registryObject instanceof RegistryPackageType)
 			return false;

 		return isLeaf;
 		
 	}

 	/*
 	 * Attribute matchOlderVersions – This optional attribute specifies the behavior 
 	 * when multiple versions of the same object are matched by a query. When the value 
 	 * of this attribute is specified as false (the default) then a server MUST only return 
 	 * the latest matched version for any object and MUST not return older versions of such 
 	 * objects even though they may match the query. 
 	 * 
 	 * When the value of this attribute is specified as true then a server MUST return all 
 	 * matched versions of all objects.
 	 */

 	private Iterator<Node> getLatestVersions(Iterator<Node> nodes) {
 		
 		// as a first step, we have to distinguish the nodes
 		// due to their logical identifier
 		
 		Map<String,ArrayList<Node>> versionedNodes = new HashMap<String, ArrayList<Node>>();
 		
 		while (nodes.hasNext()) {
 			Node node = nodes.next();

 			String lid = node.hasProperty(NEOBase.OASIS_RIM_LID) ? (String)node.getProperty(NEOBase.OASIS_RIM_LID) : null;
 			if (lid == null) continue;
 			
 			if (versionedNodes.get(lid) == null) versionedNodes.put(lid, new ArrayList<Node>());
 			versionedNodes.get(lid).add(node);
 			
 		}
 		
 		ArrayList<Node> latestNodes = new ArrayList<Node>();
 		
 		Set<String> lids = versionedNodes.keySet();
 		for (String lid:lids) {
 			
 			ArrayList<Node> nodeGroup = versionedNodes.get(lid);
 			Node latestNode = getLatestVersion(nodeGroup);
 			
 			latestNodes.add(latestNode);
 		}
 		
 			
		return latestNodes.iterator();
 		
 	}
 	
 	private Node getLatestVersion(ArrayList<Node> nodes) {

		VersionHandler vh = VersionHandler.getInstance();
 		
		Node latestNode = null;
 		String latestVersion = null;
 		
 		for (Node node:nodes) {
 			
 			Node versionInfo = vh.getVersion(node);
 		
 			String nodeVersion = versionInfo.hasProperty(NEOBase.OASIS_RIM_VERSION_NAME) ? (String)versionInfo.getProperty(NEOBase.OASIS_RIM_VERSION_NAME) : null; 
 			if (nodeVersion == null) continue;
 				
			// compare with last version
			if (compareVersions(nodeVersion, latestVersion) > 0) {

				latestNode    = node;
 				latestVersion = nodeVersion;
				
			}
 				
 		}
 		
 		return latestNode;

 	}

    /*
     * Compares 2 version Strings, with major/minor versions separated by '.'
     * Example: "1.10"
	 *
     * return int = 0 if params are equal; 
     * 		  int > 0 if 1st is greater than 2nd;
     *        int < 0 if 2st is greater than 1nd.
	 */
 	
 	private int compareVersions(String version1, String version2) {
 		
 		if (version2 == null) return 1;
 		
        String parts1 [] = version1.split("\\.", 2);
        String parts2 [] = version2.split("\\.", 2);
        
        int compare = Integer.parseInt(parts1[0]) - Integer.parseInt(parts2[0]);
        if (compare == 0) {

        	// equal.. try subversions
            if (parts1.length == 1 && parts2.length == 1) {
                // really equal
                return 0;
            
            } else if (parts1.length == 1) {
                // other is bigger (v2)
                return -1;
            
            } else if (parts2.length == 1) {
                // other is bigger (v1)
                return +1;
            
            } else {
                // try subversions
                return compareVersions(parts1[1], parts2[1]);
            }

        } else {
            return compare;

        }
 		
 	}
 	
}
