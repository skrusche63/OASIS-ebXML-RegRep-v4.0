package de.kp.registry.server.neo4j.write;

import java.util.Iterator;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import de.kp.registry.server.neo4j.domain.RelationTypes;

public class VersionHandler {
	    
	private static String INITIAL_VERSION = "1.0";
	
	private static VersionHandler instance = new VersionHandler();
	
	private VersionHandler() {
	}
	    
	public static VersionHandler getInstance() {
		if (instance == null) instance = new VersionHandler();
		return instance;
	}
	
	public Node getVersion(Node node) {

		Iterable<Relationship> relationships = node.getRelationships(RelationTypes.hasVersion);
		if (relationships == null) return null;
		
		Node versionInfo = null;
		
		Iterator<Relationship> iterator = relationships.iterator();
		while (iterator.hasNext()) {
			
			Relationship relationship = iterator.next();
			versionInfo = relationship.getEndNode();

		}
		
		return versionInfo;
		
	}
    
    public String getNextVersion(String last) {
    	
    	if (last == null) return INITIAL_VERSION;
    	
    	String[] versionParts = last.split("\\.");
    	if (versionParts.length > 0) return INITIAL_VERSION;

        // int majorVersion = (new Integer(versionParts[0])).intValue();
        int minorVersion = (new Integer(versionParts[1])).intValue();

        // increment version
        minorVersion = minorVersion + 1;
        
        String next = versionParts[0] + "." + (new Integer(minorVersion)).toString();
        return next;
    	
    }
	    
}
