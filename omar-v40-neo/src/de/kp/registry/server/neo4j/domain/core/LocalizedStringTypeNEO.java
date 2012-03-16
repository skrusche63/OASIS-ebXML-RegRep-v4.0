package de.kp.registry.server.neo4j.domain.core;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.LocalizedStringType;

import de.kp.registry.server.neo4j.domain.NEOBase;

public class LocalizedStringTypeNEO extends NEOBase {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) {
		
		LocalizedStringType localizedStringType = (LocalizedStringType)binding;
		
		// - LANGUAGE (0..1)
		String localizedStringTypeLanguage = localizedStringType.getLang();
		
		// - VALUE (1..1)
		String localizedStringTypeValue = localizedStringType.getValue();
		
		// build localizedStringType node
		Node localizedStringTypeNode = graphDB.createNode();
		
		// add internal administration properties
		localizedStringTypeNode.setProperty(NEO4J_UID, getNID());		
		localizedStringTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - LANGUAGE (0..1)
		if (localizedStringTypeLanguage != null) localizedStringTypeNode.setProperty(OASIS_RIM_LOCALE_LANG, localizedStringTypeLanguage);
		
		// - VALUE (1..1)
		localizedStringTypeNode.setProperty(OASIS_RIM_LOCALE_VALU, localizedStringTypeValue);
		
		return localizedStringTypeNode;
		
	}

	public static Object toBinding(Node node) {
		return toBinding(node, null);
	}

	public static Object toBinding(Node node, String language) {
		
		LocalizedStringType binding = factory.createLocalizedStringType();
		
		// - LANGUAGE (0..1)
		if (node.hasProperty(OASIS_RIM_LOCALE_LANG)) binding.setLang((String)node.getProperty(OASIS_RIM_LOCALE_LANG));
		
		// - VALUE (1..1)
		binding.setValue((String)node.getProperty(OASIS_RIM_LOCALE_VALU));
		
		return binding;
		
	}
	
	public static String getNType() {
		return "LocalizedStringType";
	}
}
