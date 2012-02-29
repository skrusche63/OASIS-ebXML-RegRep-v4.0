package de.kp.registry.server.neo4j.database;

import java.lang.reflect.Method;
import java.util.List;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import de.kp.registry.server.neo4j.domain.NEOBase;

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
		
		NEOBase.toNode(graphDB, binding);
				
	}
}
