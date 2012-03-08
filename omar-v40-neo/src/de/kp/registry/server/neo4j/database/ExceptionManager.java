package de.kp.registry.server.neo4j.database;

import org.oasis.ebxml.registry.bindings.rs.RegistryExceptionType;

public class ExceptionManager {

	private static ExceptionManager instance = new ExceptionManager();
	
	private ExceptionManager() {		
	}
	
	public static ExceptionManager getInstance() {
		if (instance == null) instance = new ExceptionManager();
		return instance;
	}
	
	public RegistryExceptionType toBinding(Object exception) {
		// TODO
		return null;
	}
}
