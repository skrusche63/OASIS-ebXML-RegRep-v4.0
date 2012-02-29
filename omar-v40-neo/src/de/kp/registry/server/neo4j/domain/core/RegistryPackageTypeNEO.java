package de.kp.registry.server.neo4j.domain.core;

import java.lang.reflect.Method;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectListType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;
import org.oasis.ebxml.registry.bindings.rim.RegistryPackageType;

import de.kp.registry.server.neo4j.domain.RelationTypes;

public class RegistryPackageTypeNEO extends RegistryObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
		RegistryPackageType registryPackageType = (RegistryPackageType)binding;
		
		// - REGISTRY-OBJECT-LIST (0..1)
		RegistryObjectListType registryObjectList = registryPackageType.getRegistryObjectList();

		// create node from underlying RegistryObjectType
		Node registryPackageTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a RegistryPackageType
		registryPackageTypeNode.setProperty(NEO4J_TYPE, getNType());

		if (registryObjectList != null) {
			List<RegistryObjectType>registryObjects = registryObjectList.getRegistryObject();
			for (RegistryObjectType registryObject:registryObjects) {
				
				// TODO:
				
				// (1) the registry objects may already exist
				Class<?> clazz = getClassNEO(registryObject);
			    Method method = clazz.getMethod("toNode", graphDB.getClass(), Object.class);

			    Node registryObjectTypeNode = (Node)method.invoke(null, graphDB, binding);
				registryPackageTypeNode.createRelationshipTo(registryObjectTypeNode, RelationTypes.hasMember);

			}
		}
		
		return registryPackageTypeNode;
	}

	public static Object toBinding(Node node) {
		
		RegistryPackageType binding = factory.createRegistryPackageType();
		binding = (RegistryPackageType)RegistryObjectTypeNEO.fillBinding(node, binding);
		
		return binding;
		
	}

	public static String getNType() {
		return "RegistryPackageType";
	}

	
}
