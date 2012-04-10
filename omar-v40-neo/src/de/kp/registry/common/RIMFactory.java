package de.kp.registry.common;

import java.util.UUID;

import org.oasis.ebxml.registry.bindings.rim.ClassificationType;
import org.oasis.ebxml.registry.bindings.rim.QueryDefinitionType;
import org.oasis.ebxml.registry.bindings.rim.StringQueryExpressionType;

public class RIMFactory {

	// reference to OASIS ebRIM object factory
	private static org.oasis.ebxml.registry.bindings.rim.ObjectFactory ebRIMFactory = new org.oasis.ebxml.registry.bindings.rim.ObjectFactory();

	public static ClassificationType createClassification() {
		String uid = "urn:uuid:" + UUID.randomUUID().toString();
		return createClassification(uid);
	}

	public static ClassificationType createClassification(String uid) {
		
		ClassificationType classification = ebRIMFactory.createClassificationType();
		
		classification.setId(uid);
		classification.setLid(uid);
		
		return classification;
		
	}

	public static QueryDefinitionType createQueryDefinition() {
		String uid = "urn:uuid:" + UUID.randomUUID().toString();
		return createQueryDefinition(uid);		
	}

	public static QueryDefinitionType createQueryDefinition(String uid) {
		
		QueryDefinitionType queryDefinition = ebRIMFactory.createQueryDefinitionType();
		
		queryDefinition.setId(uid);
		queryDefinition.setLid(uid);
		
		return queryDefinition;
		
	}
	
	public static StringQueryExpressionType createStringQueryExpression(String queryLanguage, String queryStatement) {
		
		StringQueryExpressionType stringQueryExpression = ebRIMFactory.createStringQueryExpressionType();
		
		stringQueryExpression.setQueryLanguage(queryLanguage);
		stringQueryExpression.setValue(queryStatement);
		
		return stringQueryExpression;
		
	}
}
