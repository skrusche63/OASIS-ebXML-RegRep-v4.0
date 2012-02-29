package de.kp.registry.server.neo4j.database;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class WriteManager {

	private static WriteManager instance = new WriteManager();
	
	private WriteManager() {		
	}
	
	public static WriteManager getInstance() {
		if (instance == null) instance = new WriteManager();
		return instance;
	}
	
	public void write(List<Object> bindings) throws Exception {
		
		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {

			for (Object binding:bindings) {
				writeInternal(graphDB, binding);				
			}
			
			tx.success();
			
		} finally {
			tx.finish();
		}

	}
	
	public void write(Object binding) throws Exception {
		
		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {
			
			writeInternal(graphDB, binding);
			tx.success();
			
		} finally {
			tx.finish();
		}

	}
	
	private void writeInternal(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {

//		Class<?> clazz = binding.getClass();
		
		// split rim class name from package
		String[] rimClazzNameParts = binding.getClass().getName().split( Pattern.quote(".") );
		String rimClazzName = rimClazzNameParts[rimClazzNameParts.length -1]; // last part
		
		// get NEO binding class for rim class
		// maybe a mapping table from rim 2 neo will be better?
		Class<?> clazz = Class.forName("de.kp.registry.server.neo4j.domain.provenance." + rimClazzName + "NEO");

		System.out.println("--> writeInternal: neoBinding class: " + clazz.getName());

	    Method method = clazz.getMethod("toNode", graphDB.getClass(), Object.class);
	    method.invoke(null, graphDB, binding);
		
	}
}
