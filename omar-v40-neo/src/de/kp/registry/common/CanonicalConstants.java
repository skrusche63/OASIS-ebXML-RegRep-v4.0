package de.kp.registry.common;

import javax.xml.namespace.QName;

public class CanonicalConstants {

	public static String DEFAULT_LANGUAGE  = "en-US";
	public static String DEFAULT_NS_PREFIX = "urn:org:acme";

	
	// The following canonical values are defined for the EventType ClassificationScheme
	public static final String CREATED 		 	= "urn:oasis:names:tc:ebxml-regrep:EventType:Created";
	public static final String UPDATED       	= "urn:oasis:names:tc:ebxml-regrep:EventType:Updated";
	public static final String DELETED 			= "urn:oasis:names:tc:ebxml-regrep:EventType:Deleted";

	
	// the following canonical values represent the return types of a query request
	public static final String LEAF_CLASS 	   = "LeafClass";
	public static final String LEAF_CLASS_RI   = "LeafClassWithRepositoryItem";
	public static final String OBJECT_REF      = "ObjectRef";
	public static final String REGISTRY_OBEJCT = "RegistryObject";

	public static final String CREDENTIAL_INFO = "urn:oasis:names:tc:ebxml-regrep:credential:info";
	public static final String CANONICAL_URI_SENDER_CERT = "urn:oasis:names:tc:ebxml-regrep:rs:security:SenderCert";
	
	// predefined user unique identifiers
	public static final String REGISTRY_GUEST    = "urn:oasis:names:tc:ebxml-regrep:predefinedusers:RegistryGuest";
	public static final String REGISTRY_OPERATOR = "urn:oasis:names:tc:ebxml-regrep:predefinedusers:RegistryOperator";

	public static final String SAML2_NAME_FORMAT = "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent";

	// namespaces
	public static final String RIM_NS  = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:4.0";
	public static final String WSSE_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
	
	// tags
	public static final String ENDPOINT_TYPE_TAG = "EndpointType";

	// qnames
	public static final QName ENDPOINT_TYPE_QNAME = new QName(RIM_NS, ENDPOINT_TYPE_TAG);
	
	// canonical object types
	public static final String CANONICAL_CS_ID_ObjectType = "urn:oasis:names:tc:ebxml-regrep:ObjectType";
	
	public static final String CANONICAL_OBJECT_TYPE_ID_WSDL      = CANONICAL_CS_ID_ObjectType + ":RegistryObjectType:ExtrinsicObjectType:WSDL";
	public static final String CANONICAL_OBJECT_TYPE_ID_XML       = CANONICAL_CS_ID_ObjectType + ":RegistryObjectType:ExtrinsicObjectType:XML";
    public static final String CANONICAL_OBJECT_TYPE_ID_XMLSchema = CANONICAL_CS_ID_ObjectType + ":RegistryObjectType:ExtrinsicObjectType:XMLSchema";

    // canonical Slot names
    public final static String CANONICAL_SLOT_WSDL_PROFILE_REFERENCED_NAMESPACES = "urn:oasis:names:tc:ebxml-regrep:profile:ws:wsdl:referencedNamespaces";
    public final static String CANONICAL_SLOT_WSDL_PROFILE_TARGET_NAMESPACE 	 = "urn:oasis:names:tc:ebxml-regrep:profile:ws:wsdl:targetNamespace";
        
    public static final String CANONICAL_SLOT_CONTENT_LOCATOR = "urn:oasis:names:tc:ebxml-regrep:SlotName:contentLocator";
    public static final String CANONICAL_SLOT_LOCATOR 		  = "urn:oasis:names:tc:ebxml-regrep:SlotName:locator";

    public static final String CANONICAL_PROTOCOL_TYPE_ID_SOAP  = "urn:oasis:names:tc:ebxml-regrep:profile:ws:ProtocolType:SOAP";         
    public static final String CANONICAL_TRANSPORT_TYPE_ID_HTTP = "urn:oasis:names:tc:ebxml-regrep:profile:ws:TransportType:HTTP";
         
    public static final String CANONICAL_SOAP_STYLE_TYPE_ID_RPC 	 = "urn:oasis:names:tc:ebxml-regrep:profile:ws:SOAPStyleType:RPC";
    public static final String CANONICAL_SOAP_STYLE_TYPE_ID_DOCUMENT = "urn:oasis:names:tc:ebxml-regrep:profile:ws:SOAPStyleType:Document";
   
    public static final String CANONICAL_OBJECT_TYPE_ID_WSDL_SERVICE 	= CANONICAL_OBJECT_TYPE_ID_WSDL + ":Service";
    public static final String CANONICAL_OBJECT_TYPE_ID_WSDL_PORT 		= CANONICAL_OBJECT_TYPE_ID_WSDL + ":Port";
    public static final String CANONICAL_OBJECT_TYPE_ID_WSDL_BINDING 	= CANONICAL_OBJECT_TYPE_ID_WSDL + ":Binding";
    public static final String CANONICAL_OBJECT_TYPE_ID_WSDL_PORT_TYPE 	= CANONICAL_OBJECT_TYPE_ID_WSDL + ":PortType";
    
	/************************************************************************
	 * 
	 * PREDEFINED QUERY DEFINITIONS    PREDEFINED QUERY DEFINITIONS    PREDEFINED
	 * 
	 ***********************************************************************/
	
	// this is the unique identifier of a parameterized query ($userId) to retrieve
	// a certain user by his unique identifier; the respective query request is used
	// in the context of the UserProvider, that queries a remote User Registry to
	// retrieve sufficient user data
	
	public static final String QUERY_GetUserById = "urn:oasis:names:tc:ebxml-regrep:QueryDefinitionType:GetUserById";
	
	// this is a query parameter to indicate the user identifier, e.g. in
	// a parameterized GetUserById query request
	
	public static final String QUERY_PARAM_USER_ID = "user";
	
	// this is the unique identifier of a parameterized query to retrieve all
	// subscriptions that match the time conditions described by startTime 
	// and endTime, the time window for valid subscriptions
	
	public static final String QUERY_GetValidSubscriptions = "urn:oasis:names:tc:ebxml-regrep:QueryDefinitionType:GetValidSubscriptions";
	
	// this is the unique identifier of a parameterized query to retrieve all
	// registry instances that are assigned as federates to a certain federation
	
	public static final String QUERY_GetFederates = "urn:oasis:names:tc:ebxml-regrep:QueryDefinitionType:GetFederates";

	// this is a query parameter to indicate the federation identifier, 
	// e.g. in parameterized GetFederates query request
	
	public static final String QUERY_PARAM_FEDERATION_ID = "federation";

	// this is a unique identifier of a parameterized query to retrieve all
	// auditable events that match a certain registry object identifier
	
	public static final String QUERY_GetAuditableEventsById = "urn:oasis:names:tc:ebxml-regrep:QueryDefinitionType:QUERY_GetAuditableEventsById";
	
	
	/************************************************************************
	 * 
	 * DYNAMICALLY SET GLOBAL CONSTANTS    DYNAMICALLY SET GLOBAL CONSTANTS
	 * 
	 ***********************************************************************/

	public static String MAX_CLOCK_SKEW = "";
	
	public static String KEYSTORE_FILE = "";
	public static String KEYSTORE_PASS = "";
	public static String KEYSTORE_TYPE = "";
	
	// global constants that are used to access the crypto module of
	// the identity provider (idp) used for securing request
	
	public static String IDP_CRYPTO  = "";
	public static String IDP_ALIAS   = "";
	public static String IDP_KEYPASS = "";

	// endpoint of the notification listener service
	// that is used to manage notifications
	
	public static String NOTIFICATION_LISTENER_URL = "";
	
	// endpoint of the user registry used to retrieve
	// user data for registering users that do not yet
	// exist in the database
	
	public static String USER_REGISTRY_URL = "";
	
	// endpoint of the federation registry used to
	// retrieve the federates of a certain federation
	
	public static String FEDERATION_REGISTRY_URL = "";
	
	// this is the path to the root of a filesystem
	// based repository associated with this server
	
	public static String REPOSITORY_ROOT = "";
	
	// postfix of the respective WSDL locations
	public static String CATALOG_WSDL      = "";
	public static String LIFECYCLE_WSDL    = "";
	public static String QUERY_WSDL        = "";	
	public static String NOTIFICATION_WSDL = "";

	// the subsequent parameters supports an email
	// based notification service
	
	public static String SMTP_AUTH = "";
	public static String SMTP_HOST = "";
	public static String SMTP_PORT = "";
	
	// user and password in case of TLS authentication
	public static String SMTP_USER     = "";
	public static String SMTP_PASSWORD = "";
	
	public static String MAIL_FROM = "";
	
	private static boolean initialized = false;
	
    static {
     
    	if (initialized == false) {
 
    		initialize();
    		initialized = true;
    	
    	}
    }

	private static void initialize() {
		
		Bundle bundle = Bundle.getInstance();
		
		// dynamic fill of keystore properties
		KEYSTORE_FILE = bundle.getString("keystore.file");
		KEYSTORE_PASS = bundle.getString("keystore.pass");
		KEYSTORE_TYPE = bundle.getString("keystore.type");
		
		// dynamic fill of idp crypto properties
		
		IDP_CRYPTO  = bundle.getString("idp.crypto.file");
		IDP_ALIAS   = bundle.getString("idp.crypto.alias");
		IDP_KEYPASS = bundle.getString("idp.crypto.keypass");
		
		// SOAP message time skews
		MAX_CLOCK_SKEW = bundle.getString("max.clock.skew");
		
		// WSDL LOCATIONS from settings
		
		CATALOG_WSDL      = bundle.getString("wsdl.catalog");
		LIFECYCLE_WSDL    = bundle.getString("wsdl.lifecycle");
		NOTIFICATION_WSDL = bundle.getString("wsdl.notification");
		QUERY_WSDL        = bundle.getString("wsdl.query");

	}
}
