package de.kp.registry.server.neo4j.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ObjectFactory;
import de.kp.registry.server.neo4j.database.Database;
import de.kp.registry.server.neo4j.write.VersionProcessor;

// the super class for all classes building the bridge
// between JAXB binding of the OASIS ebRIM and the
// respective node representation within Neo4J

public class NEOBase {

	// reference to OASIS ebRIM object factory
	public static ObjectFactory factory = new ObjectFactory();

	public static String OASIS_RIM_CODE        = "code";
	public static String OASIS_RIM_DESCRIPTION = "description";
	public static String OASIS_RIM_ID          = "id";
	public static String OASIS_RIM_LID         = "lid";
	public static String OASIS_RIM_MIMETYPE    = "mimetype";
	public static String OASIS_RIM_OWNER       = "owner";
	public static String OASIS_RIM_PATH        = "path";
	public static String OASIS_RIM_STATUS      = "status";
	public static String OASIS_RIM_TYPE        = "type";
	
	// a reference to external content by a uri
	public static String OASIS_RIM_URI         = "uri";

	// the following parameters represent the property types
	// associated with a ClassificationSchemeType

	public static String OASIS_RIM_IS_INTERNAL = "isInternal";
	public static String OASIS_RIM_NODE_TYPE   = "nodeType";
	
	// the following parameters represent the property types
	// associated with an ExternalIdentifierType

	public static String OASIS_RIM_ID_SCHEME   = "scheme";
	public static String OASIS_RIM_PARENT      = "parent";
	public static String OASIS_RIM_VALUE       = "value";
	
	// the following parameters represent the property types
	// associated with a VersionInfoType
	
	public static String OASIS_RIM_VERSION_NAME 	 = "versionName";
	public static String OASIS_RIM_VERSION_USER_NAME = "userVersionName";
	
	// the following parameters represent the property types
	// associated with a LocalizedStringType

	public static String OASIS_RIM_LOCALE_LANG = "localeLang";
	public static String OASIS_RIM_LOCALE_VALU = "localeValu";

	// the following parameters represent the property types
	// associated with a SlotType
	
	public static String OASIS_RIM_SLOT_NAME   = "slotName";
	public static String OASIS_RIM_SLOT_TYPE   = "slotType";	
	public static String OASIS_RIM_SLOT_VALU   = "slotValu";
	
	// the following parameters represent the property types
	// associated with a ClassificationType

	public static String OASIS_RIM_CLAS_NODE   = "classificationNode";
	public static String OASIS_RIM_CLAS_OBJE   = "classifiedObject";
	public static String OASIS_RIM_CLAS_SCHE   = "classificationScheme";
	public static String OASIS_RIM_NODE_REPR   = "nodeRepresentation";
	
	// the following parameters represent the property types
	// associated with a PostalAddressType

	public static String OASIS_RIM_CITY   			 = "city";
	public static String OASIS_RIM_COUNTRY   		 = "country";
	public static String OASIS_RIM_POSTAL_CODE  	 = "postalCode";
	public static String OASIS_RIM_STATE_OR_PROVINCE = "stateOrProvince";
	public static String OASIS_RIM_STREET   		 = "street";
	public static String OASIS_RIM_STREET_NUMBER   	 = "streetNumber";

	// the following parameters represent the property types
	// associated with an EmailAddressType

	public static String OASIS_RIM_EMAIL_ADDRESS   	   = "address";

	// the following parameters represent the property types
	// associated with an TelephoneNumberType

	public static String OASIS_RIM_AREA_CODE   	   = "areaCode";
	public static String OASIS_RIM_COUNTRY_CODE    = "countryCode";
	public static String OASIS_RIM_EXTENSION   	   = "extension";
	public static String OASIS_RIM_NUMBER   	   = "number";

	// the following parameters represent the property types
	// associated with a PersonNameType

	public static String OASIS_RIM_FIRST_NAME 	   = "firstName";
	public static String OASIS_RIM_LAST_NAME 	   = "lastName";
	public static String OASIS_RIM_MIDDLE_NAME 	   = "middleName";

	// the following parameters represent the property types
	// associated with a OrganizationType

	public static String OASIS_RIM_PRIMARY_CONTACT = "primaryContact";

	// the following parameters represent the property types
	// associated with a FederationType and RegistryType

	public static String OASIS_RIM_CATALOG_LATENCY 	 	= "catalogingLatency";
	public static String OASIS_RIM_CONFORMANCE_PROFILE 	= "conformanceProfile";
	public static String OASIS_RIM_REPL_SYNC_LATENCY 	= "replicationSyncLatency";
	public static String OASIS_RIM_OPERATOR				= "operator";

	// the following parameters represent the property types
	// associated with an ActionType

	public static String OASIS_RIM_EVENT_TYPE = "eventType";

	// the following parameters represent the property types
	// associated with an AuditableEventType
	
	public static String OASIS_RIM_TIMESTAMP  = "timestamp";
	public static String OASIS_RIM_REQUEST_ID = "requestId";
	public static String OASIS_RIM_USER       = "user";

	// the following parameters represent the property types
	// associated with a DeliveryInfoType

	public static String OASIS_RIM_NOTIFY_TO           = "notifyTo";
	public static String OASIS_RIM_NOTIFICATION_OPTION = "notificationOption";

	// the following parameters represent the property types
	// associated with a SubscriptionType and NotificationType
	
	public static String OASIS_RIM_ENDTIME   			 = "endtime";
	public static String OASIS_RIM_STARTTIME 			 = "starttime";
	public static String OASIS_RIM_NOTIFICATION_INTERVAL = "notificationInterval";
	public static String OASIS_RIM_SUBSCRIPTION 		 = "subscription";

	// the following parameters represent the property types
	// associated with a ServiceType and related

	public static String OASIS_RIM_ADDRESS 	         = "address";
	public static String OASIS_RIM_SERVICE_BINDING 	 = "serviceBinding";
	public static String OASIS_RIM_SERVICE_INTERFACE = "serviceInterface";

	// the following parameters represent the property types
	// associated with a ParameterType

	public static String OASIS_RIM_MIN_OCCURS     = "minOccurs";
	public static String OASIS_RIM_MAX_OCCURS     = "maxOccurs";
	public static String OASIS_RIM_DATA_TYPE      = "dataType";
	public static String OASIS_RIM_DEFAULT_VALUE  = "defaultValue";
	public static String OASIS_RIM_PARAMETER_NAME = "parameterName";

	// the following parameters represent the property types
	// associated with a QueryExpressionType

	public static String OASIS_RIM_QUERY_DEFINITION = "queryDefinition";
	public static String OASIS_RIM_QUERY_LANGUAGE   = "queryLanguage";
	public static String OASIS_RIM_QUERY_VALUE      = "value";

	// the following parameters represent the property types
	// associated with an AssociationType

	public static String OASIS_RIM_SOURCE = "sourceObject";
	public static String OASIS_RIM_TARGET = "targetObject";

	// the following parameters represent the property types
	// associated with a RoleType
	public static String OASIS_RIM_ROLE_TYPE = "roleType";

	// properties to describe a certain node uniquely within
	// neo4j
	
	public static String NEO4J_UID  = "_uid";
	public static String NEO4J_TYPE = "_type";

	public static Class<?> getClassNEO(Object binding) {
		
		String bindingName = binding.getClass().getName();
		int pos = bindingName.lastIndexOf(".");
		bindingName = bindingName.substring(pos+1);
		
		return getClassNEOByName(bindingName);
	}

	public static Class<?> getClassNEOByName(String bindingName) {
		return Database.getInstance().getMapper().get(bindingName);
	}

	// this method create a new target node from an existing source node
	// and provides a versioned unique identifier to the target
	
	public static Node cloneAndVersionNode(EmbeddedGraphDatabase graphDB, Node source) {
			
		Node target = graphDB.createNode();
		
		// clone properties
		cloneProperties(source, target);
		
		// clone relationships
		for (Relationship srel:source.getRelationships()) {

			RelationshipType relType = srel.getType();
			if (relType.equals(RelationTypes.hasVersion)) continue;
			
			Node startNode = srel.getStartNode();
			Node endNode   = srel.getEndNode();
			
			Relationship trel = source.equals(startNode) ? target.createRelationshipTo(endNode, relType) : endNode.createRelationshipTo(target, relType);

			// clone relation properties
			cloneProperties(srel, trel);

		}
		
		// update unique identifier with respective version info
		VersionProcessor vp = new VersionProcessor();
		Node sourceVersionInfo = vp.getVersion(source);

		String last = sourceVersionInfo.hasProperty(OASIS_RIM_VERSION_NAME) ? (String)sourceVersionInfo.getProperty(OASIS_RIM_VERSION_NAME) : null;
		String next = vp.getNextVersion(last);
		
        String id = source.getProperty(OASIS_RIM_LID) + ":" + next;
        target.setProperty(OASIS_RIM_ID, id);
		
        // associate target node with a new version
        Node targetVersionInfo = graphDB.createNode();
        cloneProperties(sourceVersionInfo, targetVersionInfo);
        
        targetVersionInfo.setProperty(OASIS_RIM_VERSION_NAME, next);
		target.createRelationshipTo(targetVersionInfo, RelationTypes.hasVersion);
       
		return target;
		
	}

	public static Node cloneNode(EmbeddedGraphDatabase graphDB, Node source) {
		
		Node target = graphDB.createNode();
		
		// clone properties
		cloneProperties(source, target);
		
		// clone relationships
		for (Relationship srel:source.getRelationships()) {

			RelationshipType relType = srel.getType();
			
			Node startNode = srel.getStartNode();
			Node endNode   = srel.getEndNode();
			
			Relationship trel = source.equals(startNode) ? target.createRelationshipTo(endNode, relType) : endNode.createRelationshipTo(target, relType);

			// clone relation properties
			cloneProperties(srel, trel);

		}

		return target;
		
	}

	public static void cloneProperties(PropertyContainer source, PropertyContainer target) {

		for (String key:source.getPropertyKeys()) {
			target.setProperty(key, source.getProperty(key));
		}
		
	}
	
	// this method deletes a certain relationship and optionally the respective referenced node
	
	public static Node clearRelationship(Node node, RelationshipType relationshipType, boolean reference) {
		
		Iterable<Relationship> relationships = node.getRelationships(relationshipType);
		if (relationships != null) {

			List<Object>removables = new ArrayList<Object>();

			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
				
				Relationship relationship = iterator.next();
				removables.add(relationship);
				
				Node endNode = relationship.getEndNode();
				removables.add(endNode);

			}

			// remove all collected node and relationships
			while (removables.size() > 0) {
				
				Object removable = removables.get(0);
				if (removable instanceof Node)
					if (reference == true) ((Node)removable).delete();
				
				else if (removable instanceof Relationship)
					((Relationship)removable).delete();
			}

		}

		return node;
		
	}

	// this is a helper method to retrieve a system generated identifier
	// to uniquely identify a certain node with the Neo4J database
	
	public static String getNID() {		
		return UUID.randomUUID().toString();
	}
	
	public static String getNType() {
		return null;
	}
	
}
