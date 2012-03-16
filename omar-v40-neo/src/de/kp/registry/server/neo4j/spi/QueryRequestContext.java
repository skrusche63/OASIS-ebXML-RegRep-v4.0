package de.kp.registry.server.neo4j.spi;

import java.math.BigInteger;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.oasis.ebxml.registry.bindings.query.QueryRequest;
import org.oasis.ebxml.registry.bindings.query.ResponseOptionType;
import org.oasis.ebxml.registry.bindings.rim.QueryDefinitionType;
import org.oasis.ebxml.registry.bindings.rim.QueryType;
import org.oasis.ebxml.registry.bindings.rim.SlotType;
import org.oasis.ebxml.registry.bindings.rim.StringQueryExpressionType;

import de.kp.registry.server.neo4j.database.ReadManager;

public class QueryRequestContext {

	private String comment;
	private String federation;
	private String format;
	private String language;
	
	// iterative parameters
	private BigInteger startIndex;
	private BigInteger maxResults;
	
	// query parameters
	private QueryType query;
	private BigInteger queryDepth;
	
	private ResponseOptionType responseOption;
	private Boolean matchOlderVersions;
	
	QueryRequest request;
	
	private ReadManager rm = ReadManager.getInstance();
	
	public QueryRequestContext(QueryRequest request) {
		
		this.request = request;
		
		// Attribute comment � The comment attribute if specified contains a String that describes the re-
		// quest. A server MAY save this comment within a CommentType instance and associate it with 
		// the AuditableEvent(s) for that request as described by [regrep-rim-v4.0].

		this.comment = request.getComment();

		// Element ResponseOption - This required element allows the client to control 
		// the content of the QueryResponse generated by the server in response to this 
		// request.
		//
		//		Attribute returnComposedObjects - This optional attribute specifies whether the RegistryObjects 
		//		returned should include composed objects as defined by Figure 1 in [regrep-rim-v4.0]. The default 
		//		is to return all composed objects.
		//
		//		* Attribute returnType - This optional attribute specifies the type of RegistryObject to return within 
		//		the response. Values for returnType are as follows:
		//		* ObjectRef - This option specifies that the QueryResponse MUST contain a <rim:ObjectRefList> element. 
		//		  The purpose of this option is to return references to objects rather than the actual objects.
		//		* RegistryObject - This option specifies that the QueryResponse MUST contain a  <rim:RegistryObjectList> element 
		//		  containing <rim:RegistryObject> elements with xsi:type=�rim:RegistryObjectType�.
		//		* LeafClass - This option specifies that the QueryResponse MUST contain a collection of 
		//		 <rim:RegistryObjectList> element containing <rim:RegistryObject> elements that have an 
		//		 xsi:type attribute that corresponds to leaf classes as defined in [regrep-xsd-v4.0]. No RepositoryItems 
		//		 SHOULD be included for any rim:ExtrinsicObjectType instance in the <rim:Registry-
		//		 ObjectList> element.
		//		* LeafClassWithRepositoryItem - This option is the same as the LeafClass option with the additional 
		//		  requirement that the response include the RepositoryItems, if any, for every rim:ExtrinsicObjectType 
		//		  instance in the <rim:RegistryObjectList> element.
		//
		//		If �returnType� specified does not match a result returned by the query, then the server MUST use the 
		//		closest matching semantically valid returnType that matches the result. For example, consider a case 
		//		where a Query that matches rim:OrganizationType instances is asked to return LeafClassWithRepository-
		//		Item. As this is not possible, QueryManager will assume the LeafClass option instead.
		
		this.responseOption = request.getResponseOption();

		// Element Query - This element identifies a parameterized query and supplies values 
		// for its parameters.
		
		this.query = request.getQuery();
		
		// Attribute depth - This optional attribute specifies the pre-fetch depth 
		// of the response desired by the client. A depth of 0 (default) indicates 
		// that the server MUST return only those objects that match the query. 
		
		// A depth of N where N is greater that 0 indicates that the server MUST also 
		// return objects that are reachable by N levels of references via attributes 
		// that reference other objects. 
		
		// A depth of -1 indicates that the server MUST return all objects within the 
		// transitive closure of all references from objects that matches the query.
		
		this.queryDepth = request.getDepth();

		// Attribute federation - This optional attribute specifies the id of the target
		// Federation for a federated query in case the server is a member of multiple 
		// federations. In the absence of this attribute a server must route the federated 
		// query to all registries that are a member of all federations configured within 
		// the local server. This value MUST be unspecified when a server routes a federated
		// query to another server. This is to avoid an infinite loop in federated query processing.
		
		this.federation = request.getFederation();
		
		// Attribute format - This optional attribute specifies the format of the response 
		// desired by the client. The default value is �application/x-ebrs+xml� which returns 
		// the response in ebRS QueryResponse format.
		
		// __DESIGN__ only one format application/x-ebrs+xml supported
		this.format = request.getFormat();
		
		// Attribute lang - This optional attribute specifies the natural language of the 
		// response desired by the client. The default value is to return the response with 
		// all available natural languages.
		
		this.language = request.getLang();
		
		// Attribute matchOlderVersions � This optional attribute specifies the behavior 
		// when multiple versions of the same object are matched by a query. When the value 
		// of this attribute is specified as false (the default) then a server MUST only return 
		// the latest matched version for any object and MUST not return older versions of such 
		// objects even though they may match the query. When the value of this attribute is specified 
		// as true then a server MUST return all matched versions of all objects.
		
		this.matchOlderVersions = request.isMatchOlderVersions();
		
		// Attribute maxResults - This optional attribute specifies a limit on the maximum number
		// of results the client wishes the query to return. If unspecified, the server SHOULD return 
		// either all the results, or in case the result set size exceeds a server specific limit, the 
		// server SHOULD return a sub-set of results that are within the bounds of the server specific 
		// limit. This attribute is described further in the Iterative Queries section.
		
		this.maxResults = request.getMaxResults();
		
		// Attribute startIndex - This optional integer value is used to indicate which result must be 
		// returned as the first result when iterating over a large result set. The default value is 0, 
		// which returns the result set starting
		
		this.startIndex = request.getStartIndex();

	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return this.comment;
	}

	public void setFederation(String federation) {
		this.federation = federation;
	}
	
	public String getFederation() {
		return this.federation;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
	public String getFormat() {
		return this.format;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getLanguage() {
		return this.language;
	}

	public void setMatchOlderVersions(Boolean matchOlderVersions) {
		this.matchOlderVersions = matchOlderVersions;
	}
	
	public Boolean getMatchOlderVersions() {
		return this.matchOlderVersions;
	}
	
	public void setMaxResults(BigInteger maxResults) {
		this.maxResults = maxResults;
	}
	
	public BigInteger getMaxResults() {
		return this.maxResults;
	}
	
	public void setQuery(QueryType query) {
		this.query = query;
	}
	
	public QueryType getQuery() {
		return this.query;
	}
	
	public void setQueryDepth(BigInteger queryDepth) {
		this.queryDepth = queryDepth;
	}
	
	public BigInteger getQueryDepth() {
		return this.queryDepth;
	}
	
	public void setResponseOption(ResponseOptionType responseOption) {
		this.responseOption = responseOption;
	}
	
	public ResponseOptionType getResponseOption() {
		return this.responseOption;
	}
	
	public void setStartIndex(BigInteger startIndex) {
		this.startIndex = startIndex;
	}
	
	public BigInteger getStartIndex() {
		return this.startIndex;
	}
	
	public String getReturnType() {
		return this.responseOption.getReturnType();
	}
		
 	// this helper method determines whether the request represents an iterative query
 
	// For Cypher, there is SKIP and LIMIT, together acting as a kind of
	// paging system, but executing the query again may give a different 
	// chunk of the result, so it is not sure that this works for paging
	
	public Boolean isIterativeRequest(QueryRequestContext queryContext) {
 		
 		BigInteger maxResults = queryContext.getMaxResults();
 		if (maxResults.equals(new BigInteger("-1"))) return true;
 		
 		return false;
 		
 	}
	
	// this helper method retrieves the cypher language statement
	// that represents a certain query request
	public String getCypherQuery() throws Exception {
		
		// Attribute queryDefinition � References the parameterized query 
		// to be invoked by the server. The value of this attribute MUST 
		// be a reference to a QueryDefinitionType instance that is supported
		// by the server.

		String queryDefinition = this.query.getQueryDefinition();
		
		// retrieve the referenced query definition type
		Node node = rm.findNodeByID(queryDefinition);
		if (node == null) return null;
		
		// __DESIGN__
		
		// in order to process a query request, we use the respective 
		// binding of the QueryDefinitionType node
		
		QueryDefinitionType queryDefinitionType = (QueryDefinitionType)rm.toBinding(node, null);		
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
		
		return null;
	}
	
}
