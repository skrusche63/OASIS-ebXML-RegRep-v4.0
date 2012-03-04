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

import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class PartyTypeNEO extends RegistryObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		PartyType partyType = (PartyType)binding;
		
		// - EMAIL-ADDRESS (0..*)
		List<EmailAddressType> emailAddresses = partyType.getEmailAddress();
		
		// - POSTAL-ADDRESS (0..*)
		List<PostalAddressType> postalAddresses = partyType.getPostalAddress();
		
		// - TELEPHONE-NUMBER (0..*)
		List<TelephoneNumberType> telephoneNumbers = partyType.getTelephoneNumber();
	
		// create node from underlying RegistryObjectType
		Node partyTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a PartyType
		partyTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - EMAIL-ADDRESS (0..*)
		if (emailAddresses.isEmpty() == false) {
			for (EmailAddressType emailAddress:emailAddresses) {
				
				Node emailAddressTypeNode = EmailAddressTypeNEO.toNode(graphDB, emailAddress);
				partyTypeNode.createRelationshipTo(emailAddressTypeNode, RelationTypes.hasEmailAddress);

			}
		}
		
		// - POSTAL-ADDRESS (0..*)
		if (postalAddresses.isEmpty() == false) {
			for (PostalAddressType postalAddress:postalAddresses) {
				
				Node postalAddressTypeNode = PostalAddressTypeNEO.toNode(graphDB, postalAddress);
				partyTypeNode.createRelationshipTo(postalAddressTypeNode, RelationTypes.hasPostalAddress);
				
			}
		}
		
		// - TELEPHONE-NUMBER (0..*)
		if (telephoneNumbers.isEmpty() == false) {
			for (TelephoneNumberType telephoneNumber:telephoneNumbers) {
				
				Node telephoneNumberTypeNode = TelephoneNumberTypeNEO.toNode(graphDB, telephoneNumber);
				partyTypeNode.createRelationshipTo(telephoneNumberTypeNode, RelationTypes.hasTelephoneNumber);
				
			}
		}
		
		return partyTypeNode;
		
	}

	public static Node clearNode(Node node) {

		// clear the RegistryObjectType of the respective node
		node = RegistryObjectTypeNEO.clearNode(node);
		
		// TODO
		return null;
		
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
