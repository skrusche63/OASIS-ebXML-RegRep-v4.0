package de.kp.registry.server.neo4j.domain.provenance;

import java.util.Iterator;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.PersonNameType;
import org.oasis.ebxml.registry.bindings.rim.PersonType;

import de.kp.registry.server.neo4j.domain.RelationTypes;

public class PersonTypeNEO extends PartyTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws Exception {
		
		PersonType personType = (PersonType)binding;
		
		// - PERSON-NAME (0..1)
		PersonNameType personName = personType.getPersonName();
		
		// create node from underlying PartyType
		Node personTypeNode = PartyTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a PersonType
		personTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - PERSON-NAME (0..1)
		if (personName != null) {

			Node personNameTypeNode = PersonNameTypeNEO.toNode(graphDB, personName);
			personTypeNode.createRelationshipTo(personNameTypeNode, RelationTypes.hasPersonName);

		}
		
		return personTypeNode;
	}

	public static Object toBinding(Node node) {
		
		PersonType binding = factory.createPersonType();
		binding = (PersonType)PartyTypeNEO.fillBinding(node, binding);
		
		// retrieve personName based relationships
		Iterable<Relationship> relationships = node.getRelationships(RelationTypes.hasPersonName);
		if (relationships == null) return binding;
		
		Iterator<Relationship> iterator = relationships.iterator();
		while (iterator.hasNext()) {
			// START (PersonType)--[hasPersonName]-->(PersonNameType) END
			Relationship relationship = iterator.next();
			Node personNameTypeNode = relationship.getEndNode();
						
			PersonNameType personNameType = (PersonNameType)PersonNameTypeNEO.toBinding(personNameTypeNode);
			binding.setPersonName(personNameType);
			
		}
		
		return binding;
		
	}
	
	public static String getNType() {
		return "PersonType";
	}
}
