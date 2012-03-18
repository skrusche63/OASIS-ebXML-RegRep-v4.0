package de.kp.registry.server.neo4j.auditing;

public class AuditWorker implements Runnable {
	
	private AuditContext context;
	
	public AuditWorker(AuditContext context) {
		this.context = context;
	}

	public void run() {
		
		// SubmitObjectsRequest
		// ====================
		
		// The server MUST create a single AuditableEvent object as follows:
		// 
		// * If RegistryObjects were created by the request, it contain a single 
		//   Action sub-element with eventType Created for all the RegistryObjects 
		//   created during processing of the request
		// * If RegistryObjects were updated by the request, it contain a single 
		//   Action sub-element with eventType Updated for all the RegistryObjects
		//   updated during processing of the request

		// UpdateObjectsRequest
		// ====================
		
		// The server MUST create a single AuditableEvent object as follows:
		//
		// 	* If RegistryObjects were updated by the request, it contain a single 
		//    Action sub-element with eventType Updated for all the RegistryObjects 
		//    updated during processing of the request

		
		// RemoveObjectsRequest
		// ====================

		// The server MUST create a single AuditableEvent object as follows:
		// 
		//	* If RegistryObjects were removed by the request, it contain a single 
		//    Action sub-element with eventType Deleted for all the RegistryObjects 
		//    removed during processing of the request
		
	}

}
