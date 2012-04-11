package de.kp.registry.server.neo4j.bootstrap;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.EmailAddressType;
import org.oasis.ebxml.registry.bindings.rim.PersonNameType;
import org.oasis.ebxml.registry.bindings.rim.PersonType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import de.kp.registry.common.Bundle;
import de.kp.registry.common.RIMFactory;
import de.kp.registry.server.neo4j.database.Database;
import de.kp.registry.server.neo4j.util.XMLUtil;

public class PredefinedUsers extends Bootstrap {

	private Document xmlDoc;
	
    private static final String PREF_USER_NS  = "http://www.dr-kruscheundpartner.com/predefined-users";
    private static final String PREF_USER_TAG = "PrefUser";

    // attributes
    private static final String PREF_USER_ID  = "Id";

    private static final String PREF_USER_FN  = "FirstName";
    private static final String PREF_USER_MN  = "MiddleName";
    private static final String PREF_USER_LN  = "LastName";

    private static final String PREF_USER_EA  = "EmailAddress";

	public PredefinedUsers() {
		
		// load the respective predefined users 
		// from an external xml document
		
		Bundle bundle = Bundle.getInstance();		
		String fileName = bundle.getString("predefined.user.file");
		
		try {
			this.xmlDoc = XMLUtil.parse(fileName);

		} catch (Exception e) {
			e.printStackTrace();

		}
		
	}
	
	public void createUsers() {

		List<PersonType> prefUsers = new ArrayList<PersonType>();
		
		// determine all nodes that are tagged with 'PrefUser'
	    NodeList nodes = xmlDoc.getElementsByTagNameNS(PREF_USER_NS, PREF_USER_TAG);
	    int len = nodes.getLength();
	    
	    if (len == 0) return;

	    // build binding objects
	    for (int i=0; i < len; i++) {

	    	Element elem = (Element)nodes.item(i);
	    	PersonType person = createPerson(elem);
	    
	    	prefUsers.add(person);
	    
	    }
	    
	    // register binding objects as nodes in the
	    // OASIS ebXML RegRep v4.0

		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {

			boolean result = true;			
			Boolean checkReference = false;
			
			for (PersonType prefUser:prefUsers) {					
				try {
					toNode(graphDB, prefUser, checkReference);

				} catch (Exception e) {
					result = false;
					break;
				}
				
			}
			
			if (result == true) tx.success();
			
		} finally {
			tx.finish();
		}
	    
	}

	private PersonType createPerson(Element elem) {
		
		String uid = elem.getAttribute(PREF_USER_ID);		
		PersonType person = RIMFactory.createPerson(uid);
		
		NodeList childNodes = elem.getChildNodes();
		int len = childNodes.getLength();
		
		if (len == 0) return person;
		
		// person name
		PersonNameType personName = RIMFactory.createPersonName();
		
		// email address
		EmailAddressType emailAddress = RIMFactory.createEmailAddress();
		
		for (int i=0; i < len; i++) {
			Element child = (Element)childNodes.item(i);
			
			if (child.getTagName().equals(PREF_USER_FN)) {
				String firstName = ((Text)child.getFirstChild()).getTextContent();
				personName.setFirstName(firstName);
			
			} else if (child.getTagName().equals(PREF_USER_MN)) {
				String middleName = ((Text)child.getFirstChild()).getTextContent();
				personName.setMiddleName(middleName);

			} else if (child.getTagName().equals(PREF_USER_LN)) {
				String lastName = ((Text)child.getFirstChild()).getTextContent();
				personName.setLastName(lastName);

			} else if (child.getTagName().equals(PREF_USER_EA)) {
				String address = ((Text)child.getFirstChild()).getTextContent();
				emailAddress.setAddress(address);

			}

		}
		
		// add person name
		person.setPersonName(personName);
		
		// add email address
		person.getEmailAddress().add(emailAddress);
		
		return person;
		
	}

}
