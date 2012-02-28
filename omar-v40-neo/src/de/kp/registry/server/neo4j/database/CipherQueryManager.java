package de.kp.registry.server.neo4j.database;


import java.util.Iterator;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class CipherQueryManager {

	private static CipherQueryManager instance = new CipherQueryManager();
	
	// reference to the Cipher execution engine
	ExecutionEngine engine;
	
	private CipherQueryManager() {
		
		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		engine = new ExecutionEngine(graphDB);
		
	}

	public static CipherQueryManager getInstance() {
		if (instance == null) instance = new CipherQueryManager();
		return instance;
	}

	// this is a common method to retrieve a certain node
	// from the index, identified by its 'id' property
	
	// note, that 'id' refers to the OASIS
 	public Node findNodeByID(String id) {
		
 		StringBuffer sb = new StringBuffer(); 		
 		sb.append("START ro=node:node_auto_index(id='" + id + "' RETURN ro");
		
 		ExecutionResult result = engine.execute(sb.toString());
 		Iterator<Node> nodes = result.columnAs("ro");
 		
 		Node match = null;
 		
 		while (nodes.hasNext()) {
 			match = nodes.next();
 		}
 		
 		return match;
		
	}
}
