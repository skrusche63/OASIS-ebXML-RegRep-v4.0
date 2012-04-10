package de.kp.registry.server.neo4j.bootstrap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.QueryDefinitionType;
import org.oasis.ebxml.registry.bindings.rim.StringQueryExpressionType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import de.kp.registry.common.Bundle;
import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.common.RIMFactory;
import de.kp.registry.server.neo4j.database.Database;
import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.util.XMLUtil;

public class CanonicalQueries {

	private Document xmlDoc;
	
    private static final String CANON_QUERY_NS  = "http://www.dr-kruscheundpartner.com/canoncical-queries";
    private static final String CANON_QUERY_TAG = "CanonQuery";

    private static final String CANON_QUERY_ID  = "Id";

	public CanonicalQueries() {
		
		// load the respective canonical queries 
		// from an external xml document
		
		Bundle bundle = Bundle.getInstance();		
		String fileName = bundle.getString("canonical.query.file");
		
		try {
			this.xmlDoc = XMLUtil.parse(fileName);

		} catch (Exception e) {
			e.printStackTrace();

		}
		
	}
	
	public void createQueries() {

		List<QueryDefinitionType> queries = new ArrayList<QueryDefinitionType>();
		
		// determine all nodes that are tagged with 'CanonQuery'
	    NodeList nodes = xmlDoc.getElementsByTagNameNS(CANON_QUERY_NS, CANON_QUERY_TAG);
	    int len = nodes.getLength();
	    
	    if (len == 0) return;

	    // build binding objects
	    for (int i=0; i < len; i++) {

	    	Element elem = (Element)nodes.item(i);
	    	QueryDefinitionType query = createQuery(elem);
	    
	    	queries.add(query);
	    
	    }
	    
	    // register binding objects as nodes in the
	    // OASIS ebXML RegRep v4.0

		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {

			boolean result = true;			
			Boolean checkReference = false;
			
			for (QueryDefinitionType query:queries) {					
				try {
					toNode(graphDB, query, checkReference);

				} catch (Exception e) {
					result = false;
					break;
				}
				
			}
			
			if (result == true) tx.success();
			
		} finally {
			tx.finish();
		}
	    
	}

	private QueryDefinitionType createQuery(Element elem) {
		
		String uid = elem.getAttribute(CANON_QUERY_ID);
		Text cypherText = (Text)elem.getFirstChild();
		
		String cypherStatement = cypherText.getTextContent();		
		StringQueryExpressionType stringQueryExpression = RIMFactory.createStringQueryExpression(CanonicalConstants.CYPHER_LANGUAGE, cypherStatement);
				
		QueryDefinitionType queryDefinition = RIMFactory.createQueryDefinition(uid);
		queryDefinition.setQueryExpression(stringQueryExpression);
		
		return queryDefinition;
		
	}
	
	private Node toNode(EmbeddedGraphDatabase graphDB, Object binding, Boolean checkReference) throws Exception {

		Class<?> clazz = NEOBase.getClassNEO(binding);

	    Method method = clazz.getMethod("toNode", graphDB.getClass(), Object.class, Boolean.class);
	    return (Node) method.invoke(null, graphDB, binding, checkReference);
    	
	}

}
