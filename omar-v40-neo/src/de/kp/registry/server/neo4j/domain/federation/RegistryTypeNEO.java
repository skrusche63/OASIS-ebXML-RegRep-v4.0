package de.kp.registry.server.neo4j.domain.federation;

import javax.xml.datatype.Duration;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.RegistryType;

import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;

public class RegistryTypeNEO extends RegistryObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		RegistryType registryType = (RegistryType)binding;
		
		// - CATALOGING-LATENCY (0..1)
		Duration catalogingLatency = registryType.getCatalogingLatency();
		
		// - CONFORMANCE-PROFILE (0..1)
		String conformanceProfile = registryType.getConformanceProfile();
		
		// - OPERATOR (1..1)
		String operator = registryType.getOperator();
		
		// - REPLICATION-SYN-LATENCY (0..1)
		Duration replicationSyncLatency = registryType.getReplicationSyncLatency();

		// create node from underlying RegistryObjectsType
		Node registryTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a RegistryType
		registryTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - CATALOGING-LATENCY (0..1)
		if (catalogingLatency != null) registryTypeNode.setProperty(OASIS_RIM_CATALOG_LATENCY, catalogingLatency);

		// - CONFORMANCE-PROFILE (0..1)
		if (conformanceProfile != null) registryTypeNode.setProperty(OASIS_RIM_CONFORMANCE_PROFILE, conformanceProfile);
		
		// - OPERATOR (1..1)
		registryTypeNode.setProperty(OASIS_RIM_OPERATOR, operator);
		
		// - REPLICATION-SYNC-LATENCY (0..1)
		if (replicationSyncLatency != null) registryTypeNode.setProperty(OASIS_RIM_REPL_SYNC_LATENCY, replicationSyncLatency);

		return registryTypeNode;
		
	}

	public static Object toBinding(Node node) {
	
		RegistryType binding = factory.createRegistryType();

		// - CATALOGING-LATENCY (0..1)
		if (node.hasProperty(OASIS_RIM_CATALOG_LATENCY)) binding.setCatalogingLatency((Duration)node.getProperty(OASIS_RIM_CATALOG_LATENCY));
		
		// - CONFORMANCE-PROFILE (0..1)
		if (node.hasProperty(OASIS_RIM_CONFORMANCE_PROFILE)) binding.setConformanceProfile((String)node.getProperty(OASIS_RIM_CONFORMANCE_PROFILE));
		
		// - OPERATOR (1..1)
		binding.setOperator((String)node.getProperty(OASIS_RIM_OPERATOR));
		
		// - REPLICATION-SYNC-LATENCY (0..1)
		if (node.hasProperty(OASIS_RIM_REPL_SYNC_LATENCY)) binding.setReplicationSyncLatency((Duration)node.getProperty(OASIS_RIM_REPL_SYNC_LATENCY));

		return binding;
		
	}
	
	public static String getNType() {
		return "RegistryType";
	}
}
