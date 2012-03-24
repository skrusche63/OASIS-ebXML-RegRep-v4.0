package de.kp.registry.server.neo4j.authorization;

public class AuthorizationConstants {

	public static String PERMIT_ALL  = "urn:oasis:names:tc:ebxml-regrep:AuthorizationType:PermitAll";
	public static String PERMIT_NONE = "urn:oasis:names:tc:ebxml-regrep:AuthorizationType:PermitNone";
	public static String PERMIT_SOME = "urn:oasis:names:tc:ebxml-regrep:AuthorizationType:PermitSome";

	public static String CATALOG_REQUEST = "CatalogObjectsRequest";
	public static String QUERY_REQUEST   = "QueryRequest";
	public static String REMOVE_REQUEST  = "RemoveObjectsRequest";	
	public static String SUBMIT_REQUEST  = "SubmitObjectsRequest";
	public static String UPDATE_REQUEST  = "UpdateObjectsRequest";

}
