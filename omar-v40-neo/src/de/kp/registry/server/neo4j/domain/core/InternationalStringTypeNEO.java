package de.kp.registry.server.neo4j.domain.core;

import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.InternationalStringType;
import org.oasis.ebxml.registry.bindings.rim.LocalizedStringType;

import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.RelationTypes;

public class InternationalStringTypeNEO extends NEOBase {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) {
		
		InternationalStringType internationalStringType = (InternationalStringType)binding;
		
		// - LOCALIZED-STRING (0..*)
		List<LocalizedStringType> localizedStrings = internationalStringType.getLocalizedString();
		
		// build internationaStringType node
		Node internationalStringTypeNode = graphDB.createNode();
		
		// add internal administration properties
		internationalStringTypeNode.setProperty(NEO4J_UID, getNID());		
		internationalStringTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - LOCALIZED-STRING (0..*)
		if (localizedStrings.isEmpty()) return internationalStringTypeNode;
		
		for (LocalizedStringType localizedString:localizedStrings) {

			// create a LocalizedStringType node and associate the internationalStringType node
			// via the relationship 'hasLocaleString'
			Node localizedStringTypeNode = LocalizedStringTypeNEO.toNode(graphDB, localizedString);
			internationalStringTypeNode.createRelationshipTo(localizedStringTypeNode, RelationTypes.hasLocaleString);

		}
		
		return internationalStringTypeNode;
		
	}

	public static String getNType() {
		return "InternationalStringType";
	}
	
}
