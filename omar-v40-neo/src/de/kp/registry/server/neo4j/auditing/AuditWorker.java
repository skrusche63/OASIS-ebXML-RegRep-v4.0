package de.kp.registry.server.neo4j.auditing;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.AuditableEventType;

import de.kp.registry.server.neo4j.database.Database;
import de.kp.registry.server.neo4j.domain.event.AuditableEventTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.spi.CanonicalConstants;

public class AuditWorker implements Runnable {
	
	private AuditContext context;
	
	public AuditWorker(AuditContext context) {
		this.context = context;
	}

	public void run() {
		
		// * If RegistryObjects were created by the request, it contain a single 
		//   Action sub-element with eventType Created for all the RegistryObjects 
		//   created during processing of the request
		if (this.context.isCreated()) {			
			create(this.context.getAuditableEvent(CanonicalConstants.CREATED));			
		}
		
		// * If RegistryObjects were updated by the request, it contain a single 
		//   Action sub-element with eventType Updated for all the RegistryObjects
		//   updated during processing of the request
		if (this.context.isUpdated()) {
			create(this.context.getAuditableEvent(CanonicalConstants.UPDATED));			
		}
		
		//	* If RegistryObjects were removed by the request, it contain a single 
		//    Action sub-element with eventType Deleted for all the RegistryObjects 
		//    removed during processing of the request		
		if (this.context.isDeleted()) {
			create(this.context.getAuditableEvent(CanonicalConstants.DELETED));
		}
		
	}

	private void create(AuditableEventType auditableEvent) {
		
		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {
			
			boolean checkReference = true;
			Node node = AuditableEventTypeNEO.toNode(graphDB, auditableEvent, checkReference);
			
			// TODO: comment
			
			
			tx.success();
		
		} catch (RegistryException e) {
			e.printStackTrace();

		} finally {
			tx.finish();
		}

	}
}
