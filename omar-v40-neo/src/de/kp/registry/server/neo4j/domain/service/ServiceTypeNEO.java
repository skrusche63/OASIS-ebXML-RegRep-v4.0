package de.kp.registry.server.neo4j.domain.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ServiceEndpointType;
import org.oasis.ebxml.registry.bindings.rim.ServiceType;

import de.kp.registry.server.neo4j.database.ReadManager;
import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.exception.UnresolvedReferenceException;

public class ServiceTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new ServiceType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		// create node from underlying RegistryObjectType
		Node node = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a ServiceType
		node.setProperty(NEO4J_TYPE, getNType());
		
		return fillNodeInternal(graphDB, node, binding, checkReference);
		
	}
	
	// this method replaces an existing ServiceType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier

	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return fillNode(graphDB, node, binding, checkReference, false);
	}
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference, boolean excludeVersion) throws RegistryException {
		
		// clear ServiceType specific parameters
		node = clearNode(node, excludeVersion);

		// clear & fill node with RegistryObjectType specific parameters
		node = RegistryObjectTypeNEO.fillNode(graphDB, node, binding, checkReference, excludeVersion);
		
		// fill node with ServiceType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 
		
	}

	public static Node clearNode(Node node, boolean excludeVersion) {

		// - SERVICE-ENDPOINT (0..*)

		// __DESIGN__
		
		// ServiceEndpointType nodes are an intrinsic part of a ServiceType
		// and are therefore removed in addition to the respective relationships

		// clear relationship and referenced ServiceEndpointType nodes (cascading removal)
		String deletionScope = "";
		
		boolean checkReference = false;
		boolean deleteChildren = false;
		
		node = clearServiceEndpoints(node, checkReference, deleteChildren, deletionScope);

		// - SERVICE-INTERFACE (0..1)
		if (node.hasProperty(OASIS_RIM_SERVICE_INTERFACE)) node.removeProperty(OASIS_RIM_SERVICE_INTERFACE);

		return node;
		
	}

	// this is a common wrapper to delete ServiceType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear ServiceType specific parameters
		node = clearNode(node, false);
		
		// clear node from RegistryObjectType specific parameters and remove
		RegistryObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}

	// __CASCADING REMOVAL__
	
	// this method is part of the cascading delete strategy for ServiceType nodes
	
	private static Node clearServiceEndpoints(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		Iterable<Relationship> relationships = node.getRelationships(RelationTypes.hasServiceEndpoint);
		if (relationships != null) {

			List<Object>removables = new ArrayList<Object>();

			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
				
				Relationship relationship = iterator.next();
				removables.add(relationship);
				
				Node endNode = relationship.getEndNode();
				removables.add(endNode);

			}

			// remove all collected node and relationships
			while (removables.size() > 0) {
				
				Object removable = removables.get(0);
				if (removable instanceof Node)
					// this is a dedicated removal of ServiceEndpointType node
					ServiceEndpointTypeNEO.removeNode((Node)removable, checkReference, deleteChildren, deletionScope);
				
				else if (removable instanceof Relationship)
					((Relationship)removable).delete();
			}

		}

		return node;
		
	}

	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		ServiceType serviceType = (ServiceType)binding;
		
		// - SERVICE-ENDPOINT (0..*)
		List<ServiceEndpointType> serviceEndpoints = serviceType.getServiceEndpoint();
		
		// - SERVICE-INTERFACE (0..1)
		String serviceInterface = serviceType.getServiceInterface();

		// - SERVICE-ENDPOINT (0..*)
		if (serviceEndpoints.isEmpty() == false) {
		
			for (ServiceEndpointType serviceEndpoint:serviceEndpoints) {

				Node serviceEndpointTypeNode = null;
				if (checkReference == true) {

					// we have to make sure that the referenced ServiceEndpointType
					// references an existing node in the database

					String nid = serviceEndpoint.getId();					
					serviceEndpointTypeNode = ReadManager.getInstance().findNodeByID(nid);

					if (serviceEndpointTypeNode == null) 
						throw new UnresolvedReferenceException("[ServiceType] ServiceEndpointType node with id '" + nid + "' does not exist.");		

				} else {
					serviceEndpointTypeNode = ServiceEndpointTypeNEO.toNode(graphDB, serviceEndpoint, checkReference);
					
				}

				// associate ServiceType node with respective ServiceEndpointType node
				node.createRelationshipTo(serviceEndpointTypeNode, RelationTypes.hasServiceEndpoint);

			}
			
		}
		
		// - SERVICE-INTERFACE (0..1)
		if (serviceInterface != null) {

			if (checkReference == true) {
				// make sure that the ServiceInterfaceType references an existing node within the database
				if (ReadManager.getInstance().findNodeByID(serviceInterface) == null) 
					throw new UnresolvedReferenceException("[ServiceType] ServiceInterfaceType node with id '" + serviceInterface + "' does not exist.");		

			}

			node.setProperty(OASIS_RIM_SERVICE_INTERFACE, serviceInterface);
		}
		
		return node;
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
