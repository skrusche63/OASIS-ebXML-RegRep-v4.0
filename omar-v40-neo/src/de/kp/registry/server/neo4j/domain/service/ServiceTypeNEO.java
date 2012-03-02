package de.kp.registry.server.neo4j.domain.service;

import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ServiceEndpointType;
import org.oasis.ebxml.registry.bindings.rim.ServiceType;

import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;

public class ServiceTypeNEO extends RegistryObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws Exception {
		
		ServiceType serviceType = (ServiceType)binding;
		
		// - SERVICE-ENDPOINT (0..*)
		List<ServiceEndpointType> serviceEndpoints = serviceType.getServiceEndpoint();
		
		// - SERVICE-INTERFACE (0..1)
		String serviceInterface = serviceType.getServiceInterface();
		
		// create node from underlying RegistryObjectType
		Node serviceTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a ServiceType
		serviceTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - SERVICE-ENDPOINT (0..*)
		if (serviceEndpoints.isEmpty() == false) {
		
			for (ServiceEndpointType serviceEndpoint:serviceEndpoints) {
				
				Node serviceEndpointTypeNode = ServiceEndpointTypeNEO.toNode(graphDB, serviceEndpoint);
				serviceTypeNode.createRelationshipTo(serviceEndpointTypeNode, RelationTypes.hasServiceEndpoint);

			}
			
		}
		
		// - SERVICE-INTERFACE (0..1)
		if (serviceInterface != null) serviceTypeNode.setProperty(OASIS_RIM_SERVICE_INTERFACE, serviceInterface);
		
		return serviceTypeNode;
		
	}

	public static Object toBinding(Node node) {
		
		ServiceType binding = factory.createServiceType();
		binding = (ServiceType)RegistryObjectTypeNEO.fillBinding(node, binding);

		// - SERVICE-ENDPOINT (0..*)
		Iterable<Relationship> relationships = node.getRelationships(RelationTypes.hasServiceEndpoint);
		if (relationships != null) {
		
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node serviceEndpointTypeNode = relationship.getEndNode();
				
				ServiceEndpointType serviceEndpointType = (ServiceEndpointType)ServiceEndpointTypeNEO.toBinding(serviceEndpointTypeNode);				
				binding.getServiceEndpoint().add(serviceEndpointType);

			}
			
		}

		// - SERVICE-INTERFACE (0..1)
		if (node.hasProperty(OASIS_RIM_SERVICE_INTERFACE)) binding.setServiceInterface((String)node.getProperty(OASIS_RIM_SERVICE_INTERFACE));

		return binding;
	
	}
	
	public static String getNType() {
		return "ServiceType";
	}
}
