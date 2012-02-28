package de.kp.registry.server.neo4j.domain.core;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.IdentifiableType;


public class IdentifiableTypeNEO extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		// an IdentifiableType is also an ExtensibleObjectType
		IdentifiableType identifiableType = (IdentifiableType)binding;
		
		// - IDENTIFIER (1..1)
		String identifiableTypeId = identifiableType.getId();
		
		// create node from underlying ExtensibleObjectType
		Node identifiableTypeNode = ExtensibleObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe an identifiableType
		identifiableTypeNode.setProperty(NEO4J_TYPE, getNType());
		
		// - IDENTIFIER (1..1)
		identifiableTypeNode.setProperty(OASIS_RIM_ID, identifiableTypeId);
		return identifiableTypeNode;
	}
	
	public static String getNType() {
		return "IdentifiableType";
	}
	
}
