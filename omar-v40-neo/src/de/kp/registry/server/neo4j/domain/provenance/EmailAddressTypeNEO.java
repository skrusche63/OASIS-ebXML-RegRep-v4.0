package de.kp.registry.server.neo4j.domain.provenance;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.EmailAddressType;

import de.kp.registry.server.neo4j.database.ReadManager;
import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.exception.UnresolvedReferenceException;

public class EmailAddressTypeNEO extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		EmailAddressType emailAddressType = (EmailAddressType)binding;
		
		// - ADDRESS (1..1)
		String address = emailAddressType.getAddress();
		
		// - TYPE (0..1)
		String type = emailAddressType.getType();

		// create node from underlying ExtensibleObjectType
		Node emailAddressTypeNode = ExtensibleObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe an emailAddressType
		emailAddressTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - ADDRESS (1..1)
		emailAddressTypeNode.setProperty(OASIS_RIM_EMAIL_ADDRESS, address);
		
		// - TYPE (0..1)
		if (type != null) {

			// make sure that the classification node references an existing node within the database
			if (ReadManager.getInstance().findNodeByID(type) == null) 
				throw new UnresolvedReferenceException("[EmailAddressType] Classification node with id '" + type + "' does not exist.");		
			
			emailAddressTypeNode.setProperty(OASIS_RIM_TYPE, type);

		}
		
		return emailAddressTypeNode;
		
	}
	
	// this is a common wrapper to delete an EmailAddressType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		//TODO
		node.delete();		
	}
	
	public static Object toBinding(Node node) {
		
		EmailAddressType binding = factory.createEmailAddressType();
		binding = (EmailAddressType)ExtensibleObjectTypeNEO.fillBinding(node, binding);

		// - ADDRESS (1..1)
		binding.setAddress((String)node.getProperty(OASIS_RIM_EMAIL_ADDRESS));
		
		// - TYPE (0..1)
		if (node.hasProperty(OASIS_RIM_TYPE)) binding.setType((String)node.getProperty(OASIS_RIM_TYPE));
		
		return binding;
		
	}	

	public static String getNType() {
		return "EmailAddressType";
	}
}
