package de.kp.registry.server.neo4j.domain.provenance;

import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.OrganizationType;

import de.kp.registry.server.neo4j.database.ReadManager;
import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.exception.UnresolvedReferenceException;

public class OrganizationTypeNEO extends PartyTypeNEO {

	// this method creates a new OrganizationType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		// create node from underlying PartyType
		Node node = PartyTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe an OrganizationType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);
		
	}

	// this method replaces an existing OrganizationType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return fillNode(graphDB, node, binding, checkReference, false);
	}
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference, boolean excludeVersion) throws RegistryException {
		
		// clear OrganizationType specific parameters
		node = clearNode(node, excludeVersion);
		
		// clear & fill node with PartyType specific parameters
		node = PartyTypeNEO.fillNode(graphDB, node, binding, checkReference, excludeVersion);
		
		// fill node with OrganizationType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 

	}

	public static Node clearNode(Node node, boolean excludeVersion) {
		
		// - ORGANIZATION (0..*)

		// __DESGIN__
		
		// an OrganizationType node is cleared by removing the relationships
		// to other OrganizationType nodes; the respective nodes are NOT removed
		
		// clear relationship and NOT referenced OrganizationType nodes
		node = NEOBase.clearRelationship(node, RelationTypes.hasOrganization, false);

		// - PRIMARY CONTACT (0..1)
		if (node.hasProperty(OASIS_RIM_PRIMARY_CONTACT)) node.removeProperty(OASIS_RIM_PRIMARY_CONTACT);
		
		return node;
	}

	// this is a common wrapper to delete an OrganizationType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear OrganizationType specific parameters
		node = clearNode(node, false);
		
		// clear node from PartyType specific parameters and remove
		PartyTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}

	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		OrganizationType organizationType = (OrganizationType)binding;
		
		// - ORGANIZATION (0..*)
		List<OrganizationType> organizations = organizationType.getOrganization();
		
		// - PRIMARY CONTACT (0..1)
		String primaryContact = organizationType.getPrimaryContact();

		// ===== FILL NODE =====

		// - ORGANIZATION (0..*)
		if (organizations.isEmpty() == false) {
			for (OrganizationType organization:organizations) {
				
				Node organizationTypeSubNode = null;
				if (checkReference == true) {
				
					// we have to make sure that the referenced OrganizationType
					// references an existing node in the database

					String nid = organization.getId();					
					organizationTypeSubNode = ReadManager.getInstance().findNodeByID(nid);
					
					if (organizationTypeSubNode == null) 
						throw new UnresolvedReferenceException("[OrganizationType] OrganizationType node with id '" + nid + "' does not exist.");		
					
				} else {
					organizationTypeSubNode = OrganizationTypeNEO.toNode(graphDB, organization, checkReference);
					
				}
				
				// associate subordinate organization with superior one
				node.createRelationshipTo(organizationTypeSubNode, RelationTypes.hasOrganization);

			}
		}
		
		// - PRIMARY CONTACT (0..1)
		if (primaryContact != null) {

			if (checkReference == true) {
				// make sure that the primary contact references an existing node within the database
				if (ReadManager.getInstance().findNodeByID(primaryContact) == null) 
					throw new UnresolvedReferenceException("[OrganizationType] Primary contact node with id '" + primaryContact + "' does not exist.");		

			}

			node.setProperty(OASIS_RIM_PRIMARY_CONTACT, primaryContact);
		}
		
		return node;

	}

	public static Object toBinding(Node node) {
		return toBinding(node, null);
	}

	public static Object toBinding(Node node, String language) {
	
		OrganizationType binding = factory.createOrganizationType();
		binding = (OrganizationType)PartyTypeNEO.fillBinding(node, binding, language);

		// - ORGANIZATION (0..*)
		Iterable<Relationship> relationships = node.getRelationships(RelationTypes.hasOrganization);
		if (relationships != null) {
		
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node organizationTypeNode = relationship.getEndNode();
				
				OrganizationType organizationType = (OrganizationType)OrganizationTypeNEO.toBinding(organizationTypeNode);				
				binding.getOrganization().add(organizationType);

			}
			
		}
				
		// - PRIMARY CONTACT (0..1)
		if (node.hasProperty(OASIS_RIM_PRIMARY_CONTACT)) binding.setPrimaryContact((String)node.getProperty(OASIS_RIM_PRIMARY_CONTACT));
		
		return binding;
		
	}
	
	public static String getNType() {
		return "OrganizationType";
	}
	
}
