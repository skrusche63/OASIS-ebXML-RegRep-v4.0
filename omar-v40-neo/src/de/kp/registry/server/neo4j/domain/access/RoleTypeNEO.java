package de.kp.registry.server.neo4j.domain.access;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.RoleType;

import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.federation.RegistryTypeNEO;

public class RoleTypeNEO extends RegistryObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		RoleType roleType = (RoleType)binding;
		
		// - TYPE (1..1)
		String type = roleType.getType();
		
		// create node from underlying RegistryObjectType
		Node roleTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a RoleType
		roleTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - TYPE (1..1)
		roleTypeNode.setProperty(OASIS_RIM_TYPE, type);
		
		return roleTypeNode;
		
	}

	public static Object toBinding(Node node) {
		
		RoleType binding = factory.createRoleType();
		binding = (RoleType)RegistryTypeNEO.fillBinding(node, binding);
		
		// - TYPE (1..1)
		String type = (String)node.getProperty(OASIS_RIM_TYPE);
		binding.setType(type);
		
		return binding;
		
	}

	public static String getNType() {
		return "RoleType";
	}
}
