package de.kp.registry.server.neo4j.domain.service;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ServiceEndpointType;

import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;

public class ServiceEndpointTypeNEO extends RegistryObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		ServiceEndpointType serviceEndpointType = (ServiceEndpointType)binding;
		
		// - ADDRESS (0..1)
		String address = serviceEndpointType.getAddress();
		
		// - SERVICE-BINDING (0..1)
		String serviceBinding = serviceEndpointType.getServiceBinding();
		
		// create node from underlying RegistryObjectType
		Node serviceEndpointTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a ServiceEndpointType
		serviceEndpointTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - ADDRESS (0..1)
		if (address != null) serviceEndpointTypeNode.setProperty(OASIS_RIM_ADDRESS, address);
		
		// - SERVICE-BINDING (0..1)
		if (serviceBinding != null) serviceEndpointTypeNode.setProperty(OASIS_RIM_SERVICE_BINDING, serviceBinding);
		
		return serviceEndpointTypeNode;
		
	}

	public static String getNType() {
		return "ServiceEndpointType";
	}
}
