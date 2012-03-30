package de.kp.registry.server.neo4j.service.context;

import java.util.ArrayList;
import java.util.List;

import org.oasis.ebxml.registry.bindings.rim.ExtrinsicObjectType;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefListType;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;
import org.oasis.ebxml.registry.bindings.rim.QueryType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectListType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;
import org.oasis.ebxml.registry.bindings.spi.CatalogObjectsRequest;

import de.kp.registry.server.neo4j.read.ReadManager;

public class CatalogRequestContext extends RequestContext {

	CatalogObjectsRequest request;
	
	private List<ExtrinsicObjectType> invocationControlFile;

	private List<ObjectRefType> objectRefs;
	private List<RegistryObjectType> registryObjects;
	
	public CatalogRequestContext(CatalogObjectsRequest request) {
		
		this.request = request;

		// - InvocationControlFile (specified by SERVER)
		
		// Specifies an ExtrinsicObject that is used to control the cataloging
		// process in a type specific manner. See Canonical XML Catalogor plugin 
		// for an example. This element MAY be specified by server when sending 
		// the request to the Cataloger plugin if the Cataloger plugin requires 
		// an invocation control file. It SHOULD NOT be specified by the client.
		this.invocationControlFile = request.getInvocationControlFile();
		
		// - Query (specified by CLIENT)
		
		// Specifies a query to be invoked. A server MUST catalog all objects that match
		// the specified query. This element is typically used when a client initiates 
		// the catalogObjects protocol.
		
		QueryType query = request.getQuery();

		// - ObjectRefList (specified by CLIENT)
		
		// Specifies a collection of references to existing RegistryObject instances
		// in the server. A server MUST catalog all objects that are referenced by 
		// this element. This element is typically used when a client initiates the 
		// catalogObjects protocol.

		List<ObjectRefType> objectRefs = null;
		
		ObjectRefListType refs = request.getObjectRefList();
		if (refs != null) objectRefs = refs.getObjectRef();

		// __DESIGN__
		
		// in order to catalog registry objects, we first have to merge the objects
		// stemming from the provided query, and those described by the reference list
		if (query != null) {
		
			List<ObjectRefType> queryObjectRefs = ReadManager.getInstance().getObjectRefsByQuery(query);
			
			// merge with provided objectRefs
			if (objectRefs == null) objectRefs = new ArrayList<ObjectRefType>();
			objectRefs.addAll(queryObjectRefs);
			
		}

		this.objectRefs = objectRefs;

		// - OriginalObjects (specified by SERVER)
		
		// Specifies a collection of RegistryObject instances. A server MUST catalog 
		// all objects that are contained in this element. This element is typically 
		// used when a server initiates the catalogObjects protocol during the processing 
		// of a submitObjects or updateObjects protocol request or when it is delegating 
		// a client initiated catalogObjects protocol request to a Cataloger plugin
		
		RegistryObjectListType origs = request.getOriginalObjects();
		if (origs != null) this.registryObjects = origs.getRegistryObject();
		
	}
	
	public List<ExtrinsicObjectType> getInvocationControlFile() {
		return this.invocationControlFile;
	}
	
	public List<ObjectRefType> getObjectRefs() {
		return this.objectRefs;
	}
	
	public List<RegistryObjectType> getOriginalObjects() {
		return this.registryObjects;
	}
	
}
