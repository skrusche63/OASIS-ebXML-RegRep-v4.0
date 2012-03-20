package de.kp.registry.server.neo4j.user;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.PersonType;

import de.kp.registry.server.neo4j.database.Database;
import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.provenance.PersonTypeNEO;
import de.kp.registry.server.neo4j.read.ReadManager;

public class UserUtil {

	// reference to OASIS ebRIM object factory
	public static org.oasis.ebxml.registry.bindings.rim.ObjectFactory ebRIMFactory = new org.oasis.ebxml.registry.bindings.rim.ObjectFactory();

	// this method evaluates whether a certain
	// 'id' references an existing PersonType node
	
	public static Node getUser(String id) {
		
		ReadManager rm = ReadManager.getInstance();
		Node user = rm.findNodeByID(id);
		
		if (user == null) return null;
		
		String type = (String)user.getProperty(NEOBase.NEO4J_TYPE);
		if (type.equals("PersonType")) return user;
		
		return null;
		
	}
	
	// register an authenticated user within the
	// NEO4J database
	
	public static boolean register() throws RegistryException {

		boolean result = false;
		
		// as a first step create a person binding
		PersonType binding = createPerson();
		
		// as a second step create node for person
		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();

		try {
			
			Boolean checkReference = false;
			PersonTypeNEO.toNode(graphDB, binding, checkReference);

			result = true;

			tx.success();
			
		} finally {
			tx.finish();
		}		

		return result;
	
	}
	
	private static PersonType createPerson() {

		PersonType person = ebRIMFactory.createPersonType();
		// TODO
		return person;
	
	}
	
}
