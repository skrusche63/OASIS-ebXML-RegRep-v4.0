package de.kp.registry.server.neo4j.spi;

public class CanonicalConstants {

	// The following canonical values are defined for the ResponseStatusType ClassificationScheme
	public static String FAILURE 		 = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Failure";
	public static String PARTIAL_SUCCESS = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:PartialSuccess";
	public static String SUCCESS 		 = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success";
	public static String UNAVAILABLE     = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Unavailable";
	
	// the following canonical values represent the return types of a query request
	public static String LEAF_CLASS 	 = "LeafClass";
	public static String LEAF_CLASS_RI 	 = "LeafClassWithRepositoryItem";
	public static String OBJECT_REF      = "ObjectRef";
	public static String REGISTRY_OBEJCT = "RegistryObject";

}
