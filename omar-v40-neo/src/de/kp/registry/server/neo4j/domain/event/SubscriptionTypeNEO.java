package de.kp.registry.server.neo4j.domain.event;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.SubscriptionType;

import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;

public class SubscriptionTypeNEO extends RegistryObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) {
		
		SubscriptionType subscriptionType = (SubscriptionType)binding;
		return null;
	}

	public static String getNType() {
		return "SubscriptionType";
	}
}
