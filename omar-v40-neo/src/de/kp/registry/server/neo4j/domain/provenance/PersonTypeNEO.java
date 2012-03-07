package de.kp.registry.server.neo4j.domain.provenance;

import java.util.Iterator;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.PersonNameType;
import org.oasis.ebxml.registry.bindings.rim.PersonType;

import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class PersonTypeNEO extends PartyTypeNEO {

	// this method creates a new PersonType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		// create node from underlying PartyType
		Node node = PartyTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a PersonType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);

	}

	// this method replaces an existing PersonType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		
		// clear PersonType specific parameters
		node = clearNode(node);
		
		// clear & fill node with PartyType specific parameters
		node = PartyTypeNEO.fillNode(graphDB, node, binding, checkReference);
		
		// fill node with PersonType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 

	}

	public static Node clearNode(Node node) {

		// - PERSON-NAME (0..1)
		
		// clear relationship and referenced PersonNameType nodes
		node = NEOBase.clearRelationship(node, RelationTypes.hasPersonName, true);
		return node;
		
	}

	// this is a common wrapper to delete a PersonType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear PersonType specific parameters
		node = clearNode(node);
		
		// clear node from PartyType specific parameters and remove
		PartyTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}

	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		// the parameter 'checkReference' is not evaluated for PersonType specific parameters

		PersonType personType = (PersonType)binding;
		
		// - PERSON-NAME (0..1)
		PersonNameType personName = personType.getPersonName();

		// ===== FILL NODE =====

		// - PERSON-NAME (0..1)
		if (personName != null) {

			Node personNameTypeNode = PersonNameTypeNEO.toNode(graphDB, personName, checkReference);
			node.createRelationshipTo(personNameTypeNode, RelationTypes.hasPersonName);

		}
		
		return node;

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
