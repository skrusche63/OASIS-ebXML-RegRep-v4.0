package de.kp.registry.server.neo4j.repository;

import javax.activation.DataHandler;

import org.neo4j.graphdb.Node;

/*
 * This class is responsible for managing repository items
 * that appear with a certain extrinsic object
 */
public class RepositoryManager {

	private static RepositoryManager instance = new RepositoryManager();
	
	private RepositoryManager() {		
	}
	
	public static RepositoryManager getInstance() {
		if (instance == null) instance = new RepositoryManager();
		return instance;
	}
	
	public void setItem(Node node, DataHandler repositoryItem) {
		// TODO
	}

	public void clearItem(Node node) {
		// TODO
	}

}
