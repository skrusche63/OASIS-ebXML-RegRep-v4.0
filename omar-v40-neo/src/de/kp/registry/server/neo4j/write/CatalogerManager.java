package de.kp.registry.server.neo4j.write;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Node;
import org.oasis.ebxml.registry.bindings.rim.ObjectRefType;

import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.read.ReadManager;
import de.kp.registry.server.neo4j.service.context.CatalogRequestContext;
import de.kp.registry.server.neo4j.service.context.CatalogResponseContext;
import de.kp.registry.server.neo4j.service.context.ResponseContext;

public class CatalogerManager {

	private static CatalogerManager instance = new CatalogerManager();

	// reference to OASIS ebRIM object factory
	public static org.oasis.ebxml.registry.bindings.rim.ObjectFactory ebRIMFactory = new org.oasis.ebxml.registry.bindings.rim.ObjectFactory();

	// reference to OASIS ebRS object factory
	public static org.oasis.ebxml.registry.bindings.rs.ObjectFactory ebRSFactory = new org.oasis.ebxml.registry.bindings.rs.ObjectFactory();

	private CatalogerManager() {		
	}
	
	public static CatalogerManager getInstance() {
		if (instance == null) instance = new CatalogerManager();
		return instance;
	}

	/*
	 * The server's CatalogManager SHOULD delegate catalogObjects operation to any number 
	 * of Cataloger plugins using the following algorithm:
	 * 
	 * (1) The server selects the RegistryObjects that are the target of the catalogObjects 
	 *     operations using the <spi:Query> and <rim:ObjectRefList> elements. Any objects 
	 *     specified by the OriginalObjects element MUST be ignored by the server.
	 *     
	 * (2) The server partitions the set of target objects into multiple sets based upon the 
	 *     objectType attribute value for the target objects.
	 *     
	 * (3) The server determines whether there is a Cataloger plugin configured for each objectType 
	 *     for which there is a set of target objects
	 *     
	 * (4) For each set of target objects that share a common objectType and for which there is a 
	 *     configured Cataloger plugin, the server MUST invoke the Cataloger plugin. The Cataloger 
	 *     plugin invocation MUST specify the target objects for that set using the OriginalObjects 
	 *     element. The server MUST NOT specify <spi:Query> and <rim:ObjectRefList> elements when 
	 *     invoking catalogObjects operation on a Cataloger plugin
	 *     
	 * (5) Each Cataloger plugin MUST process the CatalogObjectsRquest and return a CatalogObjects-
	 *     Response or fault message to the server's Cataloger endpoint.
	 *     
	 * (6) The server's Cataloger endpoint MUST then combine the results of the individual CatalogObjects-
	 *      Request to Cataloger plugins and commit these objects as part of the transaction associated with
	 *      the request. It MUST then combine the individual CatalogObjectsResponse messages into a single 
	 *      unified CatalogObjectsResponse and return it to the client.
	 *
	 */
	
	public ResponseContext catalogObjects(CatalogRequestContext request, CatalogResponseContext response) {
		
		List<ObjectRefType> objectRefs = request.getObjectRefs();
		if ((objectRefs == null ) || (objectRefs.size() == 0)) {
			
			// TODO: error handling
			
		} else {
			
			Map<String,List<Node>> partitionedNodes = partitionNodes(objectRefs);
			Set<String> objectTypes = partitionedNodes.keySet();
			
			// determine plugin from the key of the partitionedNodes
			CatalogerPluginFactory factory = CatalogerPluginFactory.getInstance();
			for (String objectType:objectTypes) {

				CatalogerPlugin plugin = factory.getCatalogerPlugin(objectType);
				if (plugin == null) continue;
				
				// TODO
				
			}
			
		}

		return response;
	}

	// this helper method partitions (2) target objects into a multiple
	// set of nodes distinguished by their objectType attribute
	
	private Map<String,List<Node>> partitionNodes(List<ObjectRefType> objectRefs) {
		
		ReadManager rm = ReadManager.getInstance();
		Map<String,List<Node>> partitionedNodes = new HashMap<String, List<Node>>();
		
		for (ObjectRefType objectRef:objectRefs) {
			
			Node node = rm.findNodeByID(objectRef.getId());
			if (node == null) continue;
			
			String objectType = null;
			if (node.hasProperty(NEOBase.OASIS_RIM_TYPE)) objectType = (String)node.getProperty(NEOBase.OASIS_RIM_TYPE);
		
			if (objectType == null) continue;
			
			if (partitionedNodes.get(objectType) == null) partitionedNodes.put(objectType, new ArrayList<Node>());
			partitionedNodes.get(objectType).add(node);

		}
		
		return partitionedNodes;
		
	}
}
