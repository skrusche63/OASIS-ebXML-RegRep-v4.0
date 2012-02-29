package de.kp.registry.server.neo4j.domain.core;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.IdentifiableType;

import de.kp.registry.server.neo4j.database.Database;


public class IdentifiableTypeNEO extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		// an IdentifiableType is also an ExtensibleObjectType
		IdentifiableType identifiableType = (IdentifiableType)binding;
		
		// - ID (1..1)
		String id = identifiableType.getId();
		
		// create node from underlying ExtensibleObjectType
		Node identifiableTypeNode = ExtensibleObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe an identifiableType
		identifiableTypeNode.setProperty(NEO4J_TYPE, getNType());
		
		// - ID (1..1)
		identifiableTypeNode.setProperty(OASIS_RIM_ID, id);
		
		// add node to node index 
		Database.getInstance().getNodeIndex().add(identifiableTypeNode, OASIS_RIM_ID, id);
		
		return identifiableTypeNode;
	}
	
	public static Object fillBinding(Node node, Object binding) {
		
		IdentifiableType identifiableType = (IdentifiableType)ExtensibleObjectTypeNEO.fillBinding(node, binding);

		// - ID (1..1)
		identifiableType.setId((String)node.getProperty(OASIS_RIM_ID));
		
		return identifiableType;
		
	}
	
	public static String getNType() {
		return "IdentifiableType";
	}
	
}
