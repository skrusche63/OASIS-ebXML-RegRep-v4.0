package de.kp.registry.server.neo4j.domain.provenance;

import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.OrganizationType;
import de.kp.registry.server.neo4j.domain.RelationTypes;

public class OrganizationTypeNEO extends PartyTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		OrganizationType organizationType = (OrganizationType)binding;
		
		// - ORGANIZATION (0..*)
		List<OrganizationType> organizations = organizationType.getOrganization();
		
		// - PRIMARY CONTACT (0..1)
		String primaryContact = organizationType.getPrimaryContact();
		
		// create node from underlying PartyType
		Node organizationTypeNode = PartyTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe an OrganizationType
		organizationTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - ORGANIZATION (0..*)
		if (organizations.isEmpty() == false) {
			for (OrganizationType organization:organizations) {
				
				Node organizationTypeSubNode = OrganizationTypeNEO.toNode(graphDB, organization);
				organizationTypeNode.createRelationshipTo(organizationTypeSubNode, RelationTypes.hasOrganization);

			}
		}
		
		// - PRIMARY CONTACT (0..1)
		if (primaryContact != null) organizationTypeNode.setProperty(OASIS_RIM_PRIMARY_CONTACT, primaryContact);
		
		return organizationTypeNode;
		
	}

	public static Object toBinding(Node node) {
	
		OrganizationType binding = factory.createOrganizationType();
		binding = (OrganizationType)PartyTypeNEO.fillBinding(node, binding);

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
