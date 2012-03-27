package de.kp.registry.server.neo4j.auditing;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.AuditableEventType;
import org.oasis.ebxml.registry.bindings.rim.CommentType;

import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.server.neo4j.database.Database;
import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.core.CommentTypeNEO;
import de.kp.registry.server.neo4j.domain.event.AuditableEventTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

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
			create(this.context.getAuditableEventType(CanonicalConstants.CREATED));			
		}
		
		// * If RegistryObjects were updated by the request, it contain a single 
		//   Action sub-element with eventType Updated for all the RegistryObjects
		//   updated during processing of the request
		if (this.context.isUpdated()) {
			create(this.context.getAuditableEventType(CanonicalConstants.UPDATED));			
		}
		
		//	* If RegistryObjects were removed by the request, it contain a single 
		//    Action sub-element with eventType Deleted for all the RegistryObjects 
		//    removed during processing of the request		
		if (this.context.isDeleted()) {
			create(this.context.getAuditableEventType(CanonicalConstants.DELETED));
		}
		
	}

	private void create(AuditableEventType auditableEvent) {
		
		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {
			
			boolean checkReference = true;
			Node auditableEventNode = AuditableEventTypeNEO.toNode(graphDB, auditableEvent, checkReference);
			
			// - COMMENT

			// The comment attribute if specified contains a String that describes the request.
			// A server MAY save this comment within a CommentType instance and associate it with
			// the AuditableEvent(s) for that request as described by [regrep-rim-v4.0].
			
			CommentType comment = this.context.getCommentType();
			if (comment != null) {
				
				Node commentNode = CommentTypeNEO.toNode(graphDB, comment, checkReference);
				auditableEventNode.createRelationshipTo(commentNode, RelationTypes.hasComment);
			
			}
			
			tx.success();
		
		} catch (RegistryException e) {
			e.printStackTrace();

		} finally {
			tx.finish();
		}

	}
}
