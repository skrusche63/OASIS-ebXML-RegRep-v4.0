package de.kp.registry.server.neo4j.test;


import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ObjectFactory;
import org.oasis.ebxml.registry.bindings.rim.PersonNameType;
import org.oasis.ebxml.registry.bindings.rim.PersonType;

import de.kp.registry.server.neo4j.database.Database;
import de.kp.registry.server.neo4j.database.ReadManager;
import de.kp.registry.server.neo4j.database.WriteManager;
import de.kp.registry.server.neo4j.domain.RelationTypes;

public class TestJAXB2Node extends TestCase {
	
	// reference to OASIS ebRIM object factory
	public static ObjectFactory factory = new ObjectFactory();

	/**
	 * Test simple two node one relationship graph
	 * 
	 * @throws Exception
	 */
	public void __testHello() throws Exception {
		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {
			
			Node firstNode = graphDB.createNode();
			firstNode.setProperty( "message", "Hello, " );
			
			Node secondNode = graphDB.createNode();
			secondNode.setProperty( "message", "World!" );
			 
			Relationship relationship = firstNode.createRelationshipTo( secondNode, RelationTypes.hasAction);
			relationship.setProperty( "message", "brave Neo4j " );
			
			tx.success();
			
		} finally {
			tx.finish();
		}

	}

	
	public void testWritePersonNameType() throws Exception {
		WriteManager wm = WriteManager.getInstance();

		List<Object> bindings = new ArrayList<Object>(); 
		
		PersonNameType ebPersonNameType = factory.createPersonNameType();
		ebPersonNameType.setFirstName("Peter");
		ebPersonNameType.setLastName("Arwanitis");

		PersonType ebPersonType = factory.createPersonType();
		ebPersonType.setId("de.kp.test.persontype1");
		ebPersonType.setPersonName(ebPersonNameType);
		
		bindings.add(ebPersonType);
		
		wm.write(bindings);
		
	}

	public void testReadPersonNameType() throws Exception {
		ReadManager rm = ReadManager.getInstance();

		String id = "de.kp.test.persontype1";
		Node node = rm.findNodeByID(id);
		
	}

}
