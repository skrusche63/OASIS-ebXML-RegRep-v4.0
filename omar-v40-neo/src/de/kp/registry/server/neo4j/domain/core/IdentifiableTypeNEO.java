package de.kp.registry.server.neo4j.domain.core;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.IdentifiableType;

import de.kp.registry.server.neo4j.database.Database;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;


public class IdentifiableTypeNEO extends ExtensibleObjectTypeNEO {

	// this method creates a new IdentifiableType node within database;
	// note, that we do not have to check references to other registry
	// objects, provided with this IdentifiableType

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws RegistryException {

		// create node from underlying ExtensibleObjectType
		Node node = ExtensibleObjectTypeNEO.toNode(graphDB, binding);
		node = fillNodeInternal(graphDB, node, binding);
		
		// add node to node index 
		Database.getInstance().getNodeIndex().add(node, OASIS_RIM_ID, node.getProperty(OASIS_RIM_ID));		
		return node;
		
	}

	// this method replaces an existing IdentifiableType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier

	// note, that we do not have to check references to other registry
	// objects, provided with this IdentifiableType
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding) throws RegistryException {	
		
		// there are no IdentifiableType specific parameters that must be 
		// cleared and filled afterwards
		return ExtensibleObjectTypeNEO.fillNode(graphDB, node, binding);
		
	}

	// this is a common wrapper to delete an IdentifiableType node and all of its dependencies

	public static void removeNode(Node node) {		
		ExtensibleObjectTypeNEO.removeNode(node);
	}
	
	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding) throws RegistryException {

		IdentifiableType identifiableType = (IdentifiableType)binding;
		
		// - ID (1..1)
		String id = identifiableType.getId();
		
		// ===== FILL NODE =====
		
		// - ID (1..1)
		node.setProperty(OASIS_RIM_ID, id);		
		return node;

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
