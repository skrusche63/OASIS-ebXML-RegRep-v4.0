package de.kp.registry.server.neo4j.repository;

import javax.activation.DataHandler;

import org.neo4j.graphdb.Node;

import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public interface RepositoryManager {

	public DataHandler getItem(Node node) throws RegistryException;
	
	public void setItem(Node node, DataHandler repositoryItem) throws RegistryException;

	public void clearItem(Node node) throws RegistryException;

	public boolean isExists(Node node);
	
}
