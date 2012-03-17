package de.kp.registry.server.neo4j.domain.core;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.VersionInfoType;

import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.write.VersionProcessor;

public class VersionInfoTypeNEO extends NEOBase {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) {
		
		VersionInfoType versionInfoType = (VersionInfoType)binding;
		
		// - USER VERSION NAME (0..1)
		String userVersionName = versionInfoType.getUserVersionName();
		
		// - VERSION NAME (0..1)
		String versionName = versionInfoType.getVersionName();
		
		// build versionInfoType node
		Node versionInfoTypeNode = graphDB.createNode();
		
		// add internal administration properties
		versionInfoTypeNode.setProperty(NEO4J_UID, getNID());		
		versionInfoTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - USER VERSION NAME (0..1)
		if (userVersionName != null) versionInfoTypeNode.setProperty(OASIS_RIM_VERSION_USER_NAME, userVersionName);
		
		// - VERSION NAME (0..1)
		
		// The value for this attribute SHOULD NOT be specified by the client. A server MAY silently 
		// ignore the value for this attribute if specified by the client.
		
		// The value for this attribute MUST be automatically generated by the server and MUST be defined for
		// RegistryObjectType instances returned by server responses. The server is free to choose any scheme
		// for generating the value for this attribute as long as the value is uniquely identifies a version for objects
		// that have the same lid attribute value.
		
		// __DESIGN__
		
		// the versionName is automatically set by the server
		versionName = new VersionProcessor().getNextVersion(null);
		versionInfoTypeNode.setProperty(OASIS_RIM_VERSION_NAME, versionName);
		
		return versionInfoTypeNode;
		
	}

	public static Object toBinding(Node node) {
		return toBinding(node, null);
	}

	public static Object toBinding(Node node, String language) {
	
		VersionInfoType binding = factory.createVersionInfoType();
		
		// - USER VERSION NAME (0..1)
		if (node.hasProperty(OASIS_RIM_VERSION_USER_NAME)) binding.setUserVersionName((String)node.getProperty(OASIS_RIM_VERSION_USER_NAME));
		
		// - VERSION NAME (0..1)
		if (node.hasProperty(OASIS_RIM_VERSION_NAME)) binding.setVersionName((String)node.getProperty(OASIS_RIM_VERSION_NAME));

		return binding;
		
	}
	
	
	public static String getNType() {
		return "VersionType";
	}
}
