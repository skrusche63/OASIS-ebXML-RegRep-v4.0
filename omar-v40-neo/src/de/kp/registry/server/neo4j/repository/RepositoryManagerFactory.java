package de.kp.registry.server.neo4j.repository;

public class RepositoryManagerFactory {

    private static RepositoryManagerFactory instance; 
    private RepositoryManagerFactory() {}

    public synchronized static RepositoryManagerFactory getInstance() {
        if (instance == null) instance = new RepositoryManagerFactory();
        return instance;
    }

    public RepositoryManager getRepositoryManager() {

    	// actually there is only support for a
    	// file system based repository;
    	return new FSRepositoryManager();
    	
    }
}
