package de.kp.registry.server.neo4j.domain.event;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.DeliveryInfoType;

import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;

public class DeliveryInfoTypeNEO extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) {
		
		DeliveryInfoType deliveryObjectType = (DeliveryInfoType)binding;
		return null;
	}

	public static String getNType() {
		return "DeliveryInfoType";
	}
}
