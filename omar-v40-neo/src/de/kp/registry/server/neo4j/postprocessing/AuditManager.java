package de.kp.registry.server.neo4j.postprocessing;

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
import de.kp.registry.server.neo4j.service.context.RequestContext;
import de.kp.registry.server.neo4j.service.context.ResponseContext;

public class AuditManager {

	private static AuditManager instance = new AuditManager();
	
	private AuditManager() {}
	
	public static AuditManager getInstance() {
		if (instance == null) instance = new AuditManager();
		return instance;
	}
	
	public void audit(RequestContext request, ResponseContext response) {

		AuditContext auditContext = new AuditContext(request, response);
		
		// * If RegistryObjects were created by the request, it contain a single 
		//   Action sub-element with eventType Created for all the RegistryObjects 
		//   created during processing of the request
		if (auditContext.isCreated()) {			
			create(auditContext, CanonicalConstants.CREATED);			
		}
		
		// * If RegistryObjects were updated by the request, it contain a single 
		//   Action sub-element with eventType Updated for all the RegistryObjects
		//   updated during processing of the request
		if (auditContext.isUpdated()) {
			create(auditContext, CanonicalConstants.UPDATED);			
		}
		
		//	* If RegistryObjects were removed by the request, it contain a single 
		//    Action sub-element with eventType Deleted for all the RegistryObjects 
		//    removed during processing of the request		
		if (auditContext.isDeleted()) {
			create(auditContext, CanonicalConstants.DELETED);
		}

	}

	private void create(AuditContext auditContext, String eventType) {
		
		AuditableEventType auditableEvent = auditContext.getAuditableEventType(eventType);
		
		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		Node auditableEventNode = null;
		
		try {
			
			boolean checkReference = true;
			auditableEventNode = AuditableEventTypeNEO.toNode(graphDB, auditableEvent, checkReference);
			
			// - COMMENT

			// The comment attribute if specified contains a String that describes the request.
			// A server MAY save this comment within a CommentType instance and associate it with
			// the AuditableEvent(s) for that request as described by [regrep-rim-v4.0].
			
			CommentType comment = auditContext.getCommentType();
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

		// finally we have to add the auditableEvent to
		// the response via the context data structure
		
		if (auditableEventNode != null) auditContext.setAuditableEventType(auditableEvent);
		
	}
}
