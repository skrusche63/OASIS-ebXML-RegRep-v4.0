package de.kp.registry.server.neo4j.domain.classification;

import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ClassificationNodeType;
import org.oasis.ebxml.registry.bindings.rim.TaxonomyElementType;

import de.kp.registry.server.neo4j.database.ReadManager;
import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;
import de.kp.registry.server.neo4j.domain.exception.UnresolvedReferenceException;

// This abstract type is the common base type for ClassificationSchemeType 
// and ClassificationNodeType

public class TaxonomyElementTypeNEO extends RegistryObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		// create node from underlying RegistryObjectType
		Node node = RegistryObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a TaxonomyElementType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);
		
	}

	// this method replaces an existing TaxonomyElementType node in the database
	
	// __DESIGN__ "replace" means delete and create, maintaining the unique identifier
	
	public static Node fillNode(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {		

		// clear TaxonomyElementType specific parameters
		node = clearNode(node);

		// clear & fill node with RegistryObjectType specific parameters
		node = RegistryObjectTypeNEO.fillNode(graphDB, node, binding, checkReference);
		
		// fill node with TaxonomyElementType specific parameters
		return fillNodeInternal(graphDB, node, binding, checkReference); 
	
	}

	public static Node clearNode(Node node) {
		
		// - CLASSIFICATION-NODE (0..*)

		// __DESGIN__
		
		// a TaxonomyElementType node is cleared by removing the relationships
		// to other ClassificationNodeType nodes; the respective nodes are NOT 
		// removed
		
		// clear relationship and NOT referenced ClassificationNodeType nodes
		node = NEOBase.clearRelationship(node, RelationTypes.hasChild, false);
		
		return node;
		
	}

	// this is a common wrapper to delete TaxonomyElementType node and all of its dependencies

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear TaxonomyElementType specific parameters
		node = clearNode(node);
		
		// clear node from RegistryObjectType specific parameters and remove
		RegistryObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}
	
	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {
		
		TaxonomyElementType taxonomyElementType = (TaxonomyElementType)binding;
		
		// - CLASSIFICATION-NODE (0..*)
		List<ClassificationNodeType> classificationNodes = taxonomyElementType.getClassificationNode();
		
		// ===== FILL NODE =====
				
		// - CLASSIFICATION-NODE (0..*)
		if (classificationNodes.isEmpty() == false) {

			ReadManager rm = ReadManager.getInstance();

			for (ClassificationNodeType classificationNode:classificationNodes) {

				Node classificationNodeTypeNode = null;
				if (checkReference == true) {
				
					// we have to make sure that the referenced OrganizationType
					// references an existing node in the database

					String nid = classificationNode.getId();					
					classificationNodeTypeNode = rm.findNodeByID(nid);
					
					if (classificationNodeTypeNode == null) 
						throw new UnresolvedReferenceException("[TaxonomyElementType] ClassificationNodeType node with id '" + nid + "' does not exist.");		
					
				} else {
					classificationNodeTypeNode = ClassificationNodeTypeNEO.toNode(graphDB, classificationNode, checkReference);
					
				}

				node.createRelationshipTo(classificationNodeTypeNode, RelationTypes.hasChild);

			}

		}
		
		return node;

	}

	public static Object fillBinding(Node node, Object binding) {
		
		TaxonomyElementType taxonomyElementType = (TaxonomyElementType)RegistryObjectTypeNEO.fillBinding(node, binding);
		
		// - CLASSIFICATION-NODE (0..*)
		Iterable<Relationship> relationships = node.getRelationships(RelationTypes.hasChild);
		if (relationships != null) {
		
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node classificationNodeTypeNode = relationship.getEndNode();
				
				ClassificationNodeType classificationNodeType = (ClassificationNodeType)ClassificationNodeTypeNEO.toBinding(classificationNodeTypeNode);				
				taxonomyElementType.getClassificationNode().add(classificationNodeType);

			}
			
		}

		return binding;
	}
	
	public static String getNType() {
		return "TaxonomyElementType";
	}

}
