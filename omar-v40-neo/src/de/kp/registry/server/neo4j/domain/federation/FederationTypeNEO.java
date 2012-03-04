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
		
		FederationType federationType = (FederationType)binding;
		
		// - REPLICATION-SYNC-LATENCY (0..1)
		Duration replicationSyncLatency = federationType.getReplicationSyncLatency();
		
		// create node from underlying FederationType
		Node federationTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a RegistryObjectType
		federationTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - REPLICATION-SYNC-LATENCY (0..1)
		if (replicationSyncLatency != null) federationTypeNode.setProperty(OASIS_RIM_REPL_SYNC_LATENCY, replicationSyncLatency);
		
		return federationTypeNode;
		
	}

	// this method replaces an existing FederationType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return null;
	}

	public static Node clearNode(Node node) {

		// clear the RegistryObjectType of the respective node
		node = RegistryObjectTypeNEO.clearNode(node);
		
		// TODO
		return null;
		
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
