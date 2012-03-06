package de.kp.registry.server.neo4j.domain.federation;

import javax.xml.datatype.Duration;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.RegistryType;

import de.kp.registry.server.neo4j.database.ReadManager;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.exception.UnresolvedReferenceException;

public class RegistryTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new RegistryType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {

		// create node from underlying RegistryObjectsType
		Node node = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a RegistryType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);
		
	}

	// this method replaces an existing RegistryType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		
		// clear RegistryType specific parameters
		node = clearNode(node);

		// clear & fill node with RegistryObjectType specific parameters
		node = RegistryObjectTypeNEO.fillNode(graphDB, node, binding, checkReference);
		
		// fill node with RegistryType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 
		
	}

	public static Node clearNode(Node node) {

		// - CATALOGING-LATENCY (0..1)
		if (node.hasProperty(OASIS_RIM_CATALOG_LATENCY)) node.removeProperty(OASIS_RIM_CATALOG_LATENCY);
		
		// - CONFORMANCE-PROFILE (0..1)
		if (node.hasProperty(OASIS_RIM_CONFORMANCE_PROFILE)) node.removeProperty(OASIS_RIM_CONFORMANCE_PROFILE);
		
		// - OPERATOR (1..1)
		node.removeProperty(OASIS_RIM_OPERATOR);
		
		// - REPLICATION-SYN-LATENCY (0..1)
		if (node.hasProperty(OASIS_RIM_REPL_SYNC_LATENCY)) node.removeProperty(OASIS_RIM_REPL_SYNC_LATENCY);
		
		return node;
		
	}
	
	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		RegistryType registryType = (RegistryType)binding;
		
		// - CATALOGING-LATENCY (0..1)
		Duration catalogingLatency = registryType.getCatalogingLatency();
		
		// - CONFORMANCE-PROFILE (0..1)
		String conformanceProfile = registryType.getConformanceProfile();
		
		// - OPERATOR (1..1)
		String operator = registryType.getOperator();
		
		// - REPLICATION-SYN-LATENCY (0..1)
		Duration replicationSyncLatency = registryType.getReplicationSyncLatency();
		
		// ===== FILL NODE =====

		// - CATALOGING-LATENCY (0..1)
		if (catalogingLatency != null) node.setProperty(OASIS_RIM_CATALOG_LATENCY, catalogingLatency);

		// - CONFORMANCE-PROFILE (0..1)
		if (conformanceProfile != null) node.setProperty(OASIS_RIM_CONFORMANCE_PROFILE, conformanceProfile);
		
		// - OPERATOR (1..1)
		if (checkReference == true) {
			// make sure that the operator references an existing node within the database
			if (ReadManager.getInstance().findNodeByID(operator) == null) 
				throw new UnresolvedReferenceException("[RegistryType] Operator node with id '" + operator + "' does not exist.");		

		}
		
		node.setProperty(OASIS_RIM_OPERATOR, operator);
				
		// - REPLICATION-SYNC-LATENCY (0..1)
		if (replicationSyncLatency != null) node.setProperty(OASIS_RIM_REPL_SYNC_LATENCY, replicationSyncLatency);

		return node;

	}

	public static Object toBinding(Node node) {
	
		RegistryType binding = factory.createRegistryType();
		binding = (RegistryType)RegistryObjectTypeNEO.fillBinding(node, binding);

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
