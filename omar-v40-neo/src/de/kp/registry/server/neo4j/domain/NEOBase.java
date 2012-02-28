package de.kp.registry.server.neo4j.domain;

import java.util.UUID;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ObjectFactory;

import de.kp.registry.server.neo4j.database.Database;

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

	// properties to describe a certain node uniquely within
	// neo4j
	
	public static String NEO4J_UID  = "_uid";
	public static String NEO4J_TYPE = "_type";
	
	/*
	 * create and index a Neo4J node from the respective
	 * JAXB binding object
	 */
	
	public void create() {
		
		EmbeddedGraphDatabase graphDB = Database.getInstance().getGraphDB();
		Transaction tx = graphDB.beginTx();
		
		try {
			
			Node n = graphDB.createNode();
			// do mutating operation
			tx.success();
			
		} finally {
			tx.finish();
		}
		
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
