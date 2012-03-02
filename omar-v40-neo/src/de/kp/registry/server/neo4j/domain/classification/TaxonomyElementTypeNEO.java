package de.kp.registry.server.neo4j.domain.classification;

import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ClassificationNodeType;
import org.oasis.ebxml.registry.bindings.rim.TaxonomyElementType;

import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;

// This abstract type is the common base type for ClassificationSchemeType 
// and ClassificationNodeType

public class TaxonomyElementTypeNEO extends RegistryObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws Exception {
		
		TaxonomyElementType taxonomyElementType = (TaxonomyElementType)binding;
		
		// - CLASSIFICATION-NODE (0..*)
		List<ClassificationNodeType> classificationNodes = taxonomyElementType.getClassificationNode();
		
		// create node from underlying RegistryObjectType
		Node taxonomyElementTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a TaxonomyElementType
		taxonomyElementTypeNode.setProperty(NEO4J_TYPE, getNType());
				
		// - CLASSIFICATION-NODE (0..*)
		if (classificationNodes.isEmpty() == false) {
			
			for (ClassificationNodeType classificationNode:classificationNodes) {
				
				Node classificationNodeTypeNode = ClassificationNodeTypeNEO.toNode(graphDB, classificationNode);
				taxonomyElementTypeNode.createRelationshipTo(classificationNodeTypeNode, RelationTypes.hasChild);

			}

		}
		
		return taxonomyElementTypeNode;
		
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
