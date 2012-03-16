package de.kp.registry.server.neo4j.spi;

import java.math.BigInteger;
import java.util.List;

import org.neo4j.graphdb.Node;
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
	
	private ReadManager rm = ReadManager.getInstance();
	
	public QueryRequestContext() {
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
		
		// Attribute queryDefinition – References the parameterized query 
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
		
		QueryDefinitionType queryDefinitionType = (QueryDefinitionType)rm.toBinding(node);		
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
