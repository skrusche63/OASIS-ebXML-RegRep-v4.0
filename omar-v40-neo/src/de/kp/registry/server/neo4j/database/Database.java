package de.kp.registry.server.neo4j.database;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class Database {

	private static String DB_PATH = "";
	private static Database instance = new Database();
	
	// reference to neo4j
	EmbeddedGraphDatabase graphDB;
	IndexManager indexManager;

	private Database() {
		graphDB = new EmbeddedGraphDatabase(DB_PATH);
		registerShutdownHook(graphDB);
		
		indexManager = graphDB.index();
		
	}
	
	public static Database getInstance() {
		if (instance == null) instance = new Database();
		return instance;
		
	}
	
	public EmbeddedGraphDatabase getGraphDB() {
		return this.getGraphDB();
	}
	
	public Index<Node> getNodeIndex() {
		return indexManager.forNodes("nodes");
	}
	
	public void shutdown() {
		graphDB.shutdown();
	}
	
	private static void registerShutdownHook(final GraphDatabaseService dbService) {
		
		// registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				dbService.shutdown();
			}
		});
	}
	
}
