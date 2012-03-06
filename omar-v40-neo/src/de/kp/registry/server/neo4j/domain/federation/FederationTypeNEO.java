package de.kp.registry.server.neo4j.domain.federation;

import javax.xml.datatype.Duration;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.FederationType;

import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class FederationTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new FederationType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		// create node from underlying FederationType
		Node node = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a RegistryObjectType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);
		
	}

	// this method replaces an existing FederationType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		// clear FederationType specific parameters
		node = clearNode(node);

		// clear & fill node with RegistryObjectType specific parameters
		node = RegistryObjectTypeNEO.fillNode(graphDB, node, binding, checkReference);
		
		// fill node with FederationType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 

	}

	public static Node clearNode(Node node) {
		
		// - REPLICATION-SYNC-LATENCY (0..1)
		if (node.hasProperty(OASIS_RIM_REPL_SYNC_LATENCY)) node.removeProperty(OASIS_RIM_REPL_SYNC_LATENCY);
		return node;
		
	}
	
	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		// the parameter 'checkReference' must not be evaluated
		// for a FederationType
		
		FederationType federationType = (FederationType)binding;
		
		// - REPLICATION-SYNC-LATENCY (0..1)
		Duration replicationSyncLatency = federationType.getReplicationSyncLatency();
		
		// ===== FILL NODE =====

		// - REPLICATION-SYNC-LATENCY (0..1)
		if (replicationSyncLatency != null) node.setProperty(OASIS_RIM_REPL_SYNC_LATENCY, replicationSyncLatency);
		
		return node;

	}

	public static Object toBinding(Node node) {
		
		FederationType binding = factory.createFederationType();
		binding = (FederationType)RegistryObjectTypeNEO.fillBinding(node, binding);
		
		// - REPLICATION-SYNC-LATENCY (0..1)
		if (node.hasProperty(OASIS_RIM_REPL_SYNC_LATENCY)) binding.setReplicationSyncLatency((Duration)node.getProperty(OASIS_RIM_REPL_SYNC_LATENCY));
		
		return binding;
	
	}
	
	public static String getNType() {
		return "FederationType";
	}
}
