package de.kp.registry.server.neo4j.domain.provenance;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.PersonNameType;
import org.oasis.ebxml.registry.bindings.rim.PersonType;

import de.kp.registry.server.neo4j.domain.RelationTypes;

public class PersonTypeNEO extends PartyTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) {
		
		PersonType personType = (PersonType)binding;
		
		// - PERSON-NAME (0..1)
		PersonNameType personName = personType.getPersonName();
		
		// create node from underlying PartyType
		Node personTypeNode = PartyTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a PersonType
		personTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - PERSON-NAME (0..1)
		if (personName != null) {

			Node personNameTypeNode = PersonNameTypeNEO.toNode(graphDB, personName);
			personTypeNode.createRelationshipTo(personNameTypeNode, RelationTypes.hasName);

		}
		
		return personTypeNode;
	}

	public static String getNType() {
		return "PersonType";
	}
}
