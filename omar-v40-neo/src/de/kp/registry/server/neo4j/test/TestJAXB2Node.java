package de.kp.registry.server.neo4j.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ObjectFactory;
import org.oasis.ebxml.registry.bindings.rim.PersonNameType;
import org.oasis.ebxml.registry.bindings.rim.PersonType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectListType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;
import org.oasis.ebxml.registry.bindings.rim.RegistryPackageType;

import de.kp.registry.server.neo4j.database.Database;
import de.kp.registry.server.neo4j.database.ReadManager;
import de.kp.registry.server.neo4j.database.WriteManager;
import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.RelationTypes;

/**
 * To start a clean test delete database folder
 * @author peter
 *
 */
public class TestJAXB2Node extends TestCase {

	// reference to OASIS ebRIM object factory
	public static ObjectFactory factory = new ObjectFactory();

	public TestJAXB2Node(String testName) {
		super(testName);
	}

	public static junit.framework.Test suite() throws Exception {
		// These tests need to be ordered for purposes of read/write/delete.
		// Do not change order of tests, erroneous errors will result.
		TestSuite suite = new TestSuite();

		// hello world example
//		suite.addTest(new TestJAXB2Node("testHelloWorld"));
		
		// write two nodes with relation
		suite.addTest(new TestJAXB2Node("testWritePersonNameType"));
		// read access node through index
		suite.addTest(new TestJAXB2Node("testReadPersonNameType"));
		// dump all nodes of database
		suite.addTest(new TestJAXB2Node("testDumpNodes"));
		// delete all nodes with "_type" property and their relations of database
		suite.addTest(new TestJAXB2Node("testDeleteNodes"));
		// dump all nodes of database
		suite.addTest(new TestJAXB2Node("testDumpNodes"));

		// write two nodes with relation
		suite.addTest(new TestJAXB2Node("testWriteRegistryPackageWithMultipleObjects"));
		// count all nodes of database
		suite.addTest(new TestJAXB2Node("testCountNodes"));
		// delete all nodes with "_type" property and their relations of database
		suite.addTest(new TestJAXB2Node("testDeleteNodes"));
		// count all nodes of database
		suite.addTest(new TestJAXB2Node("testCountNodes"));
		
		
		return suite;
	}

	/**
	 * Test simple two node one relationship graph
	 * 
	 * @throws Exception
	 */
	public void testHelloWorld() throws Exception {
		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();

		try {

			Node firstNode = graphDB.createNode();
			firstNode.setProperty("message", "Hello, ");

			Node secondNode = graphDB.createNode();
			secondNode.setProperty("message", "World!");

			Relationship relationship = firstNode.createRelationshipTo(secondNode, RelationTypes.hasAction);
			relationship.setProperty("message", "brave Neo4j ");

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
	
	public void testWriteRegistryPackageWithMultipleObjects() throws Exception {
		int multiple = 1000;
        long startTime = System.currentTimeMillis();

		WriteManager wm = WriteManager.getInstance();

		List<RegistryObjectType> bindings = new ArrayList<RegistryObjectType>();

		RegistryPackageType ebRegistryPackageType = factory.createRegistryPackageType();
		ebRegistryPackageType.setId("de.kp.test.registrypackage");
		
		RegistryObjectListType ebRegistryObjectListType = factory.createRegistryObjectListType();
		
		PersonNameType ebPersonNameType = factory.createPersonNameType();
		ebPersonNameType.setFirstName("Peter");
		ebPersonNameType.setLastName("Arwanitis");

		PersonType ebPersonType;
	    for (int i=0; i<multiple; i++) {
			ebPersonType = factory.createPersonType();
			// counted identifier
			ebPersonType.setId("de.kp.test.persontype" + i);
			ebPersonType.setPersonName(ebPersonNameType);

			bindings.add(ebPersonType);
	    }
	    
	    ebRegistryObjectListType.getRegistryObject().addAll(bindings);
 	    ebRegistryPackageType.setRegistryObjectList(ebRegistryObjectListType);
	    
		wm.write(ebRegistryPackageType);

        long endTime = System.currentTimeMillis();
		System.out.println("\n>>> Write " + multiple + " PersonTypes with relation to PersonNameType in: <" + (endTime-startTime) + " msec>");

	}
	


	public void testReadPersonNameType() throws Exception {
		ReadManager rm = ReadManager.getInstance();
		WriteManager wm = WriteManager.getInstance();

		/*
		 * test if query for existent node works
		 */
		String id = "de.kp.test.persontype1";
		Node node = rm.findNodeByID(id);
		assertTrue("Cannot find node", node.hasProperty(NEOBase.NEO4J_TYPE));
		
		/*
		 * test if node has correct id
		 */
		assertTrue("Node id doesn't fit", node.getProperty(NEOBase.OASIS_RIM_ID).equals(id));

		/*
		 * test node can be converted back to rim binding
		 */
		// get NEO wrapper class from node type
		// call toBinding()
		PersonType ebPersonType = (PersonType) rm.toBinding(node);
		assertTrue("Cannot getLastName() from binding: " + node.getProperty(NEOBase.NEO4J_TYPE), 
				ebPersonType.getPersonName().getLastName().equals("Arwanitis"));
		
		/*
		 * test if query for nonexistent node fails
		 */
		id = "de.kp.test.notexistent";
		node = rm.findNodeByID(id);
		assertTrue("Did not get null for nonexistent node. node=" + node, node == null);
		
		/*
		 * test that query for nodes with duplicated IDs fails with getSingle()
		 */
	    for (int i=0; i<2; i++) {
	    	ebPersonType = factory.createPersonType();
	    	ebPersonType.setId("de.kp.test.persontype.same");
	    	wm.write(ebPersonType);
	    }
		id = "de.kp.test.persontype.same";
		try {
			node = rm.findNodeByID(id);
			fail("Did not throw exception for duplicate node lookup.");
		} catch (Exception e) {
			// expect, let through
		}
		
	}

	public void testDumpNodes() {

		// readonly access without transaction
		System.out.println("\n>>> Start dump");

		Iterator<Node> nodes = Database.getInstance().getGraphDB().getAllNodes().iterator();
		while (nodes.hasNext()) {

			Node node = nodes.next();
			
			System.out.println("  id(" + node.getId() + ") type(" + (node.hasProperty(NEOBase.NEO4J_TYPE) ? node.getProperty(NEOBase.NEO4J_TYPE) : "__internal__") + ") hasRelations<" + node.hasProperty(NEOBase.NEO4J_TYPE) + ">");

		}
		System.out.println("<<< End dump");

	}

	public void testCountNodes() {

		// readonly access without transaction
		countNodes();
	}
	
	public void testDeleteNodes() {

		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		int countNodes = 0;
		int countRelationships = 0;
		try {

			Iterator<Node> nodes = graphDB.getAllNodes().iterator();

			System.out.println("\n>>> Start delete");
			while (nodes.hasNext()) {

				Node node = nodes.next();

				if (node.hasProperty("_type")) {
					// System.out.println("  DEL id(" + node.getId() + ") hasRelations<" + node.hasProperty(NEOBase.NEO4J_TYPE) + ">");

					// remove all relations from that node
					if (node.hasRelationship()) {
						Iterator<Relationship> relationships = node.getRelationships().iterator();
						while (relationships.hasNext()) {
							relationships.next().delete();
							countRelationships++;
						}
					}

					// delete node itself
					node.delete();
					countNodes ++;

				}

				tx.success();

			}
		} finally {
			System.out.println("<<< End deleted " + countNodes + " nodes and " + countRelationships + " relationship");
			tx.finish();
		}

	}

	private int countNodes() {
		int count = 0;
        long startTime = System.currentTimeMillis();
		Iterator<Node> nodes = Database.getInstance().getGraphDB().getAllNodes().iterator();
		while (nodes.hasNext()) {
			if (nodes.next().hasProperty(NEOBase.NEO4J_TYPE)) count++;
		}
        long endTime = System.currentTimeMillis();
        System.err.println("==> count(" + count + ") in: <" + (endTime-startTime) + " msec>");
		return count;
	}

}
