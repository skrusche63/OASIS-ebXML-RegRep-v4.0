package de.kp.registry.server.neo4j.domain.provenance;

import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.EmailAddressType;
import org.oasis.ebxml.registry.bindings.rim.PartyType;
import org.oasis.ebxml.registry.bindings.rim.PostalAddressType;
import org.oasis.ebxml.registry.bindings.rim.TelephoneNumberType;

import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class PartyTypeNEO extends RegistryObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		// create node from underlying RegistryObjectType
		Node node = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a PartyType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);
		
	}

	// this method replaces an existing PartyType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return fillNode(graphDB, node, binding, checkReference, false);
	}
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference, boolean excludeVersion) throws RegistryException {
		
		// clear PartyType specific parameters
		node = clearNode(node, excludeVersion);
		
		// clear & fill node with RegistryObjectType specific parameters
		node = RegistryObjectTypeNEO.fillNode(graphDB, node, binding, checkReference, excludeVersion);
		
		// fill node with PartyType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 
	
	}

	public static Node clearNode(Node node, boolean excludeVersion) {
		
		// - EMAIL-ADDRESS (0..*)
		
		// clear relationship and referenced EmailAddressType nodes
		node = NEOBase.clearRelationship(node, RelationTypes.hasEmailAddress, true);

		// - POSTAL-ADDRESS (0..*)

		// clear relationship and referenced PostalAddressType nodes
		node = NEOBase.clearRelationship(node, RelationTypes.hasPostalAddress, true);

		// - TELEPHONE-NUMBER (0..*)

		// clear relationship and referenced TelephoneNumberType nodes
		node = NEOBase.clearRelationship(node, RelationTypes.hasTelephoneNumber, true);

		return node;
		
	}

	// this is a common wrapper to delete a PartyType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear PartyType specific parameters
		node = clearNode(node, false);
		
		// clear node fromRegistryObjectType specific parameters and remove
		RegistryObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);

	}

	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		// the parameter 'checkReference' is not evaluated for PartyType specific parameters
		
		PartyType partyType = (PartyType)binding;
		
		// - EMAIL-ADDRESS (0..*)
		List<EmailAddressType> emailAddresses = partyType.getEmailAddress();
		
		// - POSTAL-ADDRESS (0..*)
		List<PostalAddressType> postalAddresses = partyType.getPostalAddress();
		
		// - TELEPHONE-NUMBER (0..*)
		List<TelephoneNumberType> telephoneNumbers = partyType.getTelephoneNumber();

		// ===== FILL NODE =====

		// - EMAIL-ADDRESS (0..*)
		if (emailAddresses.isEmpty() == false) {
			for (EmailAddressType emailAddress:emailAddresses) {
				
				Node emailAddressTypeNode = EmailAddressTypeNEO.toNode(graphDB, emailAddress, checkReference);
				node.createRelationshipTo(emailAddressTypeNode, RelationTypes.hasEmailAddress);

			}
		}
		
		// - POSTAL-ADDRESS (0..*)
		if (postalAddresses.isEmpty() == false) {
			for (PostalAddressType postalAddress:postalAddresses) {
				
				Node postalAddressTypeNode = PostalAddressTypeNEO.toNode(graphDB, postalAddress, checkReference);
				node.createRelationshipTo(postalAddressTypeNode, RelationTypes.hasPostalAddress);
				
			}
		}
		
		// - TELEPHONE-NUMBER (0..*)
		if (telephoneNumbers.isEmpty() == false) {
			for (TelephoneNumberType telephoneNumber:telephoneNumbers) {
				
				Node telephoneNumberTypeNode = TelephoneNumberTypeNEO.toNode(graphDB, telephoneNumber, checkReference);
				node.createRelationshipTo(telephoneNumberTypeNode, RelationTypes.hasTelephoneNumber);
				
			}
		}
		
		return node;

	}

	public static Object fillBinding(Node node, Object binding) {
		
		PartyType partyType = (PartyType) RegistryObjectTypeNEO.fillBinding(node, binding);
		Iterable<Relationship> relationships = null;
		
		// - EMAIL-ADDRESS (0..*)
		relationships = node.getRelationships(RelationTypes.hasEmailAddress);
		if (relationships != null) {
		
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node emailAddressTypeNode = relationship.getEndNode();
				
				EmailAddressType emailAddressType = (EmailAddressType)EmailAddressTypeNEO.toBinding(emailAddressTypeNode);				
				partyType.getEmailAddress().add(emailAddressType);

			}
			
		}

		// - POSTAL-ADDRESS (0..*)
		relationships = node.getRelationships(RelationTypes.hasPostalAddress);
		if (relationships != null) {
		
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node postalAddressTypeNode = relationship.getEndNode();
				
				PostalAddressType postalAddressType = (PostalAddressType)PostalAddressTypeNEO.toBinding(postalAddressTypeNode);				
				partyType.getPostalAddress().add(postalAddressType);

			}
			
		}

		// - TELEPHONE-NUMBER (0..*)
		relationships = node.getRelationships(RelationTypes.hasTelephoneNumber);
		if (relationships != null) {
		
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node telephoneNumberTypeNode = relationship.getEndNode();
				
				TelephoneNumberType telephoneNumberType = (TelephoneNumberType)TelephoneNumberTypeNEO.toBinding(telephoneNumberTypeNode);				
				partyType.getTelephoneNumber().add(telephoneNumberType);

			}
			
		}

		return partyType;
	}
	
	public static String getNType() {
		return "PartyType";
	}
}
