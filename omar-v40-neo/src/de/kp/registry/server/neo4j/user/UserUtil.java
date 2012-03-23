package de.kp.registry.server.neo4j.user;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.PersonType;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.NameID;

import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.common.CredentialInfo;
import de.kp.registry.server.neo4j.database.Database;
import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.provenance.PersonTypeNEO;
import de.kp.registry.server.neo4j.read.ReadManager;
import de.kp.registry.server.neo4j.service.context.RequestContext;

public class UserUtil {

	// reference to OASIS ebRIM object factory
	public static org.oasis.ebxml.registry.bindings.rim.ObjectFactory ebRIMFactory = new org.oasis.ebxml.registry.bindings.rim.ObjectFactory();


    // this method sets the user of the current request from the 
	// provided SAML assertion; if the user instance is locally 
	// not existing, it is created as a replica from the respective 
	// remote user
    
    public static void setCallersUser(RequestContext request) {
    	
    	String user = null;
		try {
			user = get(request);            
	        if (user == null) user = register(request);

		} catch (RegistryException e) {
			// do nothing
		}
        
        if (user == null) request.setUser(CanonicalConstants.REGISTRY_GUEST);

    }
    
	// this method evaluates whether a certain
	// 'id' references an existing PersonType node
	
	public static Node getById(String id) {
		
		ReadManager rm = ReadManager.getInstance();
		Node user = rm.findNodeByID(id);
		
		if (user == null) return null;
		
		String type = (String)user.getProperty(NEOBase.NEO4J_TYPE);
		if (type.equals("PersonType")) return user;
		
		return null;
		
	}
	
	// register an authenticated user within the
	// NEO4J database
	
	public static String register(RequestContext request) throws RegistryException {

		String user = null;
		
		// as a first step create a person binding
		PersonType binding = createPerson();
		
		// as a second step create node for person
		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();

		try {
			
			Boolean checkReference = false;
			PersonTypeNEO.toNode(graphDB, binding, checkReference);

			tx.success();
			
		} finally {
			tx.finish();
		}		

		return user;
	
	}

	// this method retrieves a unique user identifier from
	// the SAML assertion provided with the credential info

    public static String get(RequestContext request) {
    	
    	String user = null;
    	
    	CredentialInfo credentialInfo = request.getCredentialInfo();
    	if (credentialInfo == null) return user;
    	
    	Assertion assertion = credentialInfo.getAssertion();
    	if (assertion == null) return user;

		NameID nameId = assertion.getSubject().getNameID();
		if (nameId.getFormat().equals(CanonicalConstants.SAML2_NAME_FORMAT)) {
			
			user = nameId.getValue();
			
			// search user in the database
			Node node = getById(user);
			if (node == null) return null;
			
			
		}

        return user;
        
    }
  
	private static PersonType createPerson() {

		PersonType person = ebRIMFactory.createPersonType();
		// TODO
		return person;
	
	}

}
