package de.kp.registry.server.neo4j.domain.access;

import java.lang.reflect.Method;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.lcm.UpdateActionType;
import org.oasis.ebxml.registry.bindings.rim.RoleType;

import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.federation.RegistryTypeNEO;
import de.kp.registry.server.neo4j.domain.util.UpdateActionListType;

public class RoleTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new RoleType node within database
	
	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		// create node from underlying RegistryObjectType
		Node node = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a RoleType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);
		
	}

	// this method replaces an existing RoleType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		
		// clear RoleType specific parameters
		node = clearNode(node);
		
		// clear & fill node with RegistryObjectType specific parameters
		node = RegistryObjectTypeNEO.fillNode(graphDB, node, binding, checkReference);
		
		// fill node with RoleType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 
	
	}

	public static Node clearNode(Node node) {
		
		// - TYPE (1..1)
		node.removeProperty(OASIS_RIM_ROLE_TYPE);		
		return node;
		
	}

	// this is a common wrapper to delete RoleType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear RoleType specific parameters
		node = clearNode(node);
		
		// clear node from RegistryObjectType specific parameters and remove
		RegistryObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}
	
	public static void updateNode(Node node, Boolean checkReference, UpdateActionListType updateActionList) {
		
		List<UpdateActionType> updateActions = updateActionList.getUpdateAction();
		
	}

	public static void versionNode(Node node, boolean checkReference, String status) {
		
	}
	
	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		
		// __DESIGN__
		
		// the parameter 'checkReference' must not be evaluated for RoleType
		
		RoleType roleType = (RoleType)binding;
		
		// - TYPE (1..1)
		String type = roleType.getType();

		// ===== FILL NODE =====

		// - TYPE (1..1)
		node.setProperty(OASIS_RIM_ROLE_TYPE, type);
		
		return node;
		
	}
	
	public static Object toBinding(Node node) {
		
		RoleType binding = factory.createRoleType();
		binding = (RoleType)RegistryTypeNEO.fillBinding(node, binding);
		
		// - TYPE (1..1)
		binding.setType((String)node.getProperty(OASIS_RIM_ROLE_TYPE));
		
		return binding;
		
	}

	public static String getNType() {
		return "RoleType";
	}
}
