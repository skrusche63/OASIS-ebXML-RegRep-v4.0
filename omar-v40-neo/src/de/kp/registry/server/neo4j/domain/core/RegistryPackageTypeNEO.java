package de.kp.registry.server.neo4j.domain.core;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectListType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;
import org.oasis.ebxml.registry.bindings.rim.RegistryPackageType;

import de.kp.registry.server.neo4j.database.ReadManager;
import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.exception.UnresolvedReferenceException;

public class RegistryPackageTypeNEO extends RegistryObjectTypeNEO {

	// this method creates a new RegistryPackageType node within database

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {

		// create node from underlying RegistryObjectType
		Node node = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a RegistryPackageType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);

	}

	// this method replaces an existing RegistryPackageType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		return fillNode(graphDB, node, binding, checkReference, false);
	}
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference, boolean excludeVersion) throws RegistryException {

		// clear RegistyPackageType specific parameters
		node = clearNode(node, excludeVersion);
		
		// clear & fill node with RegistryObjectType specific parameters
		node = RegistryObjectTypeNEO.fillNode(graphDB, node, binding, checkReference, excludeVersion);
		
		// fill node with RegistryPackageType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 
		
	}

	public static Node clearNode(Node node, boolean excludeVersion) {
		
		// - REGISTRY-OBJECT-LIST (0..1)

		// __DESIGN__
		
		// a RegistryObjectType node is no intrinsic part of a RegistryPackageType node;
		// therefore the associated RegistryObjectType must not be deleted
		
		// clear relationship only
		node = NEOBase.clearRelationship(node, RelationTypes.hasMember, false);
		
		return node;
		
	}

	// this is a common wrapper to delete RegistryPackageType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear RegistryPackageType specific parameters
		node = clearNode(node, false);
		
		// clear node from RegistryObjectType specific parameters and remove
		RegistryObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}

	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		RegistryPackageType registryPackageType = (RegistryPackageType)binding;
		
		// - REGISTRY-OBJECT-LIST (0..1)
		RegistryObjectListType registryObjectList = registryPackageType.getRegistryObjectList();

		if (registryObjectList != null) {
			List<RegistryObjectType>registryObjects = registryObjectList.getRegistryObject();
			for (RegistryObjectType registryObject:registryObjects) {

				String nid = registryObject.getId();					
				Node registryObjectTypeNode = ReadManager.getInstance().findNodeByID(nid);

				if (registryObjectTypeNode == null) {
					
					if (checkReference == true)
						throw new UnresolvedReferenceException("[RegistryPackageType] RegistryObjectType node with id '" + nid + "' does not exist.");		
					
					else {

						Class<?> clazz = getClassNEO(registryObject);
					    Method method;
						try {

							method = clazz.getMethod("toNode", graphDB.getClass(), Object.class, boolean.class);
							registryObjectTypeNode = (Node)method.invoke(null, graphDB, registryObject, checkReference);

						} catch (Exception e) {
							//TODO
						} 

					}
					
				}

				node.createRelationshipTo(registryObjectTypeNode, RelationTypes.hasMember);

			}
		}
		
		return node;		
	}

	public static Object toBinding(Node node) {
		
		RegistryPackageType binding = factory.createRegistryPackageType();
		binding = (RegistryPackageType)RegistryObjectTypeNEO.fillBinding(node, binding);

		// - REGISTRY-OBJECT-LIST (0..1)
		Iterable<Relationship> relationships = node.getRelationships(RelationTypes.hasMember);
		if (relationships != null) {
		
			RegistryObjectListType registryObjectTypeList = factory.createRegistryObjectListType();
			
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node registryObjectTypeNode = relationship.getEndNode();

				try {

					Class<?> clazz = getClassNEO(registryObjectTypeNode.getProperty(NEO4J_TYPE));
					Method method = clazz.getMethod("toBinding", Node.class);
					
					RegistryObjectType registryObjectType = (RegistryObjectType)method.invoke(null, registryObjectTypeNode);
					registryObjectTypeList.getRegistryObject().add(registryObjectType);
					
				} catch (Exception e) {
					e.printStackTrace();
					
				}

			}

			binding.setRegistryObjectList(registryObjectTypeList);
			
		}

		return binding;
		
	}

	public static String getNType() {
		return "RegistryPackageType";
	}
	
}
