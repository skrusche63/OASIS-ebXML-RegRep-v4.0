package de.kp.registry.common;

public class CanonicalConstants {

	public static String DEFAULT_LANGUAGE = "en-US";
	
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
	
	public static final String QUERY_PARAM_USER_ID = "userId";
	
	public static String MAX_CLOCK_SKEW = "";
	
	public static String KEYSTORE_FILE = "";
	public static String KEYSTORE_PASS = "";
	public static String KEYSTORE_TYPE = "";
	
	// global constants that are used to access the crypto module of
	// the identity provider (idp) used for securing request
	
	public static String IDP_CRYPTO  = "";
	public static String IDP_ALIAS   = "";
	public static String IDP_KEYPASS = "";

	// endpoint of the user registry used to retrieve
	// user data for registering users that do not yet
	// exist in the database
	
	public static String USER_REGISTRY_URL = "";
	
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
		
	}
}
