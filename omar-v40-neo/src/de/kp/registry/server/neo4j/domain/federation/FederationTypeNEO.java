package de.kp.registry.server.neo4j.domain.federation;

import javax.xml.datatype.Duration;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.FederationType;

import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;

public class FederationTypeNEO extends RegistryObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		FederationType federationType = (FederationType)binding;
		
		// - REPLICATION-SYNC-LATENCY (0..1)
		Duration replicationSyncLatency = federationType.getReplicationSyncLatency();
		
		// create node from underlying FederationType
		Node federationTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a RegistryObjectType
		federationTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - REPLICATION-SYNC-LATENCY (0..1)
		if (replicationSyncLatency != null) federationTypeNode.setProperty(OASIS_RIM_REPL_SYNC_LATENCY, replicationSyncLatency);
		
		return federationTypeNode;
		
	}

	public static String getNType() {
		return "FederationType";
	}
}
