package de.kp.registry.server.neo4j.notification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.oasis.ebxml.registry.bindings.rim.QueryDefinitionType;
import org.oasis.ebxml.registry.bindings.rim.StringQueryExpressionType;
import org.oasis.ebxml.registry.bindings.rim.SubscriptionType;

import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.server.neo4j.domain.event.SubscriptionTypeNEO;
import de.kp.registry.server.neo4j.read.ReadManager;

/*
 * This class retrieves all submissions that are potentially affected 
 * by the actual request: Attributes startTime and endTime define the 
 * time window within which the subscription is valid.
 */

public class SubscriptionManager {

	private ReadManager rm = ReadManager.getInstance();

	private static SubscriptionManager instance = new SubscriptionManager();
	
	private SubscriptionManager() {	
	}
	
	public static SubscriptionManager getInstance() {
		if (instance == null) instance = new SubscriptionManager();
		return instance;
	}
	
	// this method retrieves all subscriptions registered within the
	// OASIS ebXML RegRep, which have a time window that matches the
	// current time
	
	public List<SubscriptionType> getSubscriptions() {
		
		List<SubscriptionType> subscriptions = null;
		
		try {
			
			String cypherQuery = getCypherQuery();
			if (cypherQuery == null) return subscriptions;
			
			Iterator<Node> queryResult = rm.executeCypherQuery(cypherQuery);
			while (queryResult.hasNext()) {
				
				Node node = queryResult.next();
				SubscriptionType subscription = (SubscriptionType)SubscriptionTypeNEO.toBinding(node);
				
				if (subscriptions == null) subscriptions = new ArrayList<SubscriptionType>();
				subscriptions.add(subscription);
				
			}
			
			
		} catch (Exception e) {
			// do nothing
		}
		
		return subscriptions;
		
	}
	
	// this method retrieves the cipher statement that is used to
	// determine all subscriptions that match the time conditions
	// described by startTime and endTime
	
	private String getCypherQuery() throws Exception {

		String queryDefinition = CanonicalConstants.QUERY_GetValidSubscriptions;
		
		// retrieve the referenced query definition type
		
		Node node = rm.findNodeByID(queryDefinition);
		if (node == null) return null;
		
		// __DESIGN__
		
		// in order to process a query request, we use the respective 
		// binding of the QueryDefinitionType node
		
		QueryDefinitionType queryDefinitionType = (QueryDefinitionType)rm.toBinding(node, null);		
		StringQueryExpressionType queryExpressionType = (StringQueryExpressionType)queryDefinitionType.getQueryExpression(); 

		if ("CYPHER".equals(queryExpressionType.getQueryLanguage()) == false) return null;
		return queryExpressionType.getValue();

	}
	
	// OLD STUFF
	
    /*
     * Gets the Map of Subscriptions that definitely match the specified event.
     * Map.Entry key = subscription object
     * Map.Entry value = subscribed objects Collection
     */
	/*
    HashMap getMatchedSubscriptionsMap(ServerRequestContext context, AuditableEventType ae) throws RegistryException {
        
        HashMap matchedSubscriptionsMap = new HashMap();
        
        queryToObjectsMap.clear();
       
        List matchedQuerys = getMatchedQuerys(context, ae);

        if (matchedQuerys.size() > 0) {
            StringBuffer ids = BindingUtility.getInstance().getIdListFromRegistryObjects(matchedQuerys);

            //Get all Subscriptions that use the matched querys as selectors        
            String query = "SELECT s.* FROM Subscription s WHERE s.selector IN ( " + ids + " )";
            List objectRefs = new ArrayList();
            List matchedSubscriptions = pm.executeSQLQuery(context, query, responseOption, "Subscription", objectRefs);
            
            Iterator iter = matchedSubscriptions.iterator();
            while (iter.hasNext()) {
                SubscriptionType subscription = (SubscriptionType)iter.next();
                
                matchedSubscriptionsMap.put(subscription, queryToObjectsMap.get(subscription.getSelector()));
            }
        }
        
        return matchedSubscriptionsMap;
    }
    */

    /**
     * Gets the List of AdhocQuery that match the specified event. 
     * Initialized the matchedQueryMap with queries matching the event
     * as keys and objects matching the query as values.
     *
     */

	/*
	private List getMatchedQuerys(ServerRequestContext context, AuditableEventType ae) throws RegistryException {
        List matchedQuerys = new ArrayList();
        
        List targetQuerys = getTargetQuerys(context, ae);
        List affectedObjects = ae.getAffectedObjects().getObjectRef();
        
        Iterator iter = targetQuerys.iterator();
        while (iter.hasNext()) {
            AdhocQueryType query = (AdhocQueryType)iter.next();
            
            if (queryMatches(context, query, ae.getId(), affectedObjects)) {
                matchedQuerys.add(query);                
            }
        }
        return matchedQuerys;
    }
    */
	
    /*
     * Determines whether a specified target (potentially matching) query 
     * actually matches the list of affectedObjects or not.
     *
     */
	
	/*
    private boolean queryMatches(ServerRequestContext context, AdhocQueryType query, String currentEventId, List affectedObjects) throws RegistryException {
        boolean match = false;        
        
        QueryExpressionType queryExp = query.getQueryExpression();
        String queryLang = queryExp.getQueryLanguage();
        if (queryLang.equals(BindingUtility.CANONICAL_QUERY_LANGUAGE_ID_SQL_92)) {
            String queryStr = (String)queryExp.getContent().get(0);

            //Special parameter replacement for $currentEventId
            queryStr = queryStr.replaceAll("\\$currentEventId", currentEventId);
            
            //Get objects that match the selector query (selectedObjects)
            List selectedObjects = pm.executeSQLQuery(context, queryStr, responseOption, "RegistryObject", objectRefs);
            
            //match is true if the affectedObjects  are a sub-set of the selectedObjects
            List selectedObjectIds = BindingUtility.getInstance().getIdsFromRegistryObjects(selectedObjects);
            List affectedObjectIds = BindingUtility.getInstance().getIdsFromRegistryObjects(affectedObjects);
            
            if (selectedObjectIds.size() > 0) {
                Iterator iter = affectedObjectIds.iterator();
                while (iter.hasNext()) {
                    if (selectedObjectIds.contains(iter.next())) {
                        match = true;
                        break;
                    }                    
                }
            }
            
            //Now remember which objects matched this query
            queryToObjectsMap.put(query.getId(), selectedObjects);
        }
        else {
            throw new RegistryException(ServerResourceBundle.getInstance().getString("message.onlySQLQuerySupported"));
        }
        
        return match;
    }
    */
    
    /*
     * Gets the List of AdhocQueries that potentially match the specifid event.
     * This is an essential filtering mechanism to achieve scalability by narrowing
     * the number of Subscriptions to test for a match.
     * <p>
     * Gets all AdhocQuerys that have event type and primary partition 
     * matching this event.
     *
     * TODO: We need to get the List of objectType for affectedObjects somehow
     * to help the filtering. This is a spec issue at the moment because AuditableEvent
     * only contains ObjectRefs and not the actual objects.
     *
     * </p>
     */
	
	/*
    private List getTargetQuerys(ServerRequestContext context, AuditableEventType ae) throws RegistryException {   
        List querys= null;
        
        try {
            String eventType = ae.getEventType();

            ResponseOptionType responseOption = BindingUtility.getInstance().queryFac.createResponseOption();
            responseOption.setReturnType(ReturnType.LEAF_CLASS);
            List objectRefs = new ArrayList();

            //TODO: Filter down further based upon primary partitions in future to scale better.

            //Get those AdhocQuerys that match the events eventType or have no eventType specified
            String query = "SELECT q.* FROM AdhocQuery q, Subscription s WHERE q.id = s.selector AND ((q.query LIKE '%eventType%=%" + eventType + "%') OR (q.query NOT LIKE '%eventType%=%'))";
            querys = pm.executeSQLQuery(context, query, responseOption, "AdhocQuery", objectRefs);

        }
        catch (javax.xml.bind.JAXBException e) {
            throw new RegistryException(e);
        }
        
        return querys;

*/
}
