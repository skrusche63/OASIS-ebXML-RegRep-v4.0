package de.kp.registry.server.neo4j.domain.classification;

import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.ClassificationNodeType;
import org.oasis.ebxml.registry.bindings.rim.TaxonomyElementType;

import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;

// This abstract type is the common base type for ClassificationSchemeType 
// and ClassificationNodeType

public class TaxonomyElementTypeNEO extends RegistryObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) {
		
		TaxonomyElementType taxonomyElementType = (TaxonomyElementType)binding;
		
		// - CLASSIFICATION-NODE (0..1)
		List<ClassificationNodeType> classificationNodes = taxonomyElementType.getClassificationNode();
		
		// create node from underlying RegistryObjectType
		Node taxonomyElementTypeNode = RegistryObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a TaxonomyElementType
		taxonomyElementTypeNode.setProperty(NEO4J_TYPE, getNType());
				
		// - CLASSIFICATION-NODE (0..1)
		if (classificationNodes.isEmpty() == false) {
			
			for (ClassificationNodeType classificationNode:classificationNodes) {
				
				Node classificationNodeTypeNode = ClassificationNodeTypeNEO.toNode(graphDB, classificationNode);
				taxonomyElementTypeNode.createRelationshipTo(classificationNodeTypeNode, RelationTypes.hasChild);

			}

		}
		
		return taxonomyElementTypeNode;
		
	}

	public static String getNType() {
		return "TaxonomyElementType";
	}

}
