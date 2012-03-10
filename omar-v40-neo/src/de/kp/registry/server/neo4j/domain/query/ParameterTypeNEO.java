package de.kp.registry.server.neo4j.domain.query;

import java.math.BigInteger;
import java.util.Iterator;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.InternationalStringType;
import org.oasis.ebxml.registry.bindings.rim.ParameterType;

import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.core.InternationalStringTypeNEO;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class ParameterTypeNEO extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding, boolean checkReference) throws RegistryException {
		
		// create node from underlying ExtensibleObjectType
		Node node = ExtensibleObjectTypeNEO.toNode(graphDB, binding, checkReference);
		
		// update the internal type to describe a ParameterType
		node.setProperty(NEO4J_TYPE, getNType());

		return fillNodeInternal(graphDB, node, binding, checkReference);
		
	}

	public static Node clearNode(Node node, boolean excludeVersion) {

		// - DATA-TYPE (1..1)
		node.removeProperty(OASIS_RIM_DATA_TYPE);	
		
		// - DEFAULT-VALUE (0..1)
		if (node.hasProperty(OASIS_RIM_DEFAULT_VALUE)) node.removeProperty(OASIS_RIM_DEFAULT_VALUE);
		
		// - DESCRIPTION (0..1)

		// clear relationship and referenced InternationalStringType node
		node = NEOBase.clearRelationship(node, RelationTypes.hasDescription, true);

		// - MIN-OCCURS (0..1)
		if (node.hasProperty(OASIS_RIM_MIN_OCCURS)) node.removeProperty(OASIS_RIM_MIN_OCCURS);
		
		// - MAX-OCCURS (0..1)
		if (node.hasProperty(OASIS_RIM_MAX_OCCURS)) node.removeProperty(OASIS_RIM_MAX_OCCURS);
		
		// - NAME (1..1)		

		// clear relationship and referenced InternationalStringType node
		node = NEOBase.clearRelationship(node, RelationTypes.hasName, true);
		
		// - PARAMETER-NAME (1..1)
		node.removeProperty(OASIS_RIM_PARAMETER_NAME);

		return node;

	}

	public static void removeNode(Node node, boolean checkReference, boolean deleteChildren, String deletionScope) {
		
		// clear ParameterType specific parameters
		node = clearNode(node, false);
		
		// clear node from ExtensibleObjectType specific parameters and remove
		ExtensibleObjectTypeNEO.removeNode(node, checkReference, deleteChildren, deletionScope);
		
	}
	
	private static Node fillNodeInternal(EmbeddedGraphDatabase graphDB, Node node, Object binding, boolean checkReference) throws RegistryException {

		// the parameter 'checkReference' must not be evaluated for ParameterType node
		
		ParameterType parameterType = (ParameterType)binding;
		
		// - DATA-TYPE (1..1)
		String dataType = parameterType.getDataType();
		
		// - DEFAULT-VALUE (0..1)
		String defaultValue = parameterType.getDefaultValue();
		
		// - DESCRIPTION (0..1)
		InternationalStringType description = parameterType.getDescription();
		
		// - MIN-OCCURS (0..1)
		BigInteger minOccurs = parameterType.getMinOccurs();
		
		// - MAX-OCCURS (0..1)
		BigInteger maxOccurs = parameterType.getMaxOccurs();
		
		// - NAME (1..1)
		InternationalStringType name = parameterType.getName();
		
		// - PARAMETER-NAME (1..1)
		String parameterName = parameterType.getParameterName();
		
		
		// ===== FILL NODE =====

		// - DATA-TYPE (1..1)
		node.setProperty(OASIS_RIM_DATA_TYPE, dataType);	
		
		// - DEFAULT-VALUE (0..1)
		if (defaultValue != null) node.setProperty(OASIS_RIM_DEFAULT_VALUE, defaultValue);
		
		// - DESCRIPTION (0..1)
		if (description != null) {
			
			Node internationalStringTypeNode = InternationalStringTypeNEO.toNode(graphDB, description);
			node.createRelationshipTo(internationalStringTypeNode, RelationTypes.hasDescription);

		}

		// - MIN-OCCURS (0..1)
		if (minOccurs != null) node.setProperty(OASIS_RIM_MIN_OCCURS, minOccurs);
		
		// - MAX-OCCURS (0..1)
		if (maxOccurs != null) node.setProperty(OASIS_RIM_MAX_OCCURS, maxOccurs);
		
		// - NAME (1..1)		
		Node internationalStringTypeNode = InternationalStringTypeNEO.toNode(graphDB, name);
		node.createRelationshipTo(internationalStringTypeNode, RelationTypes.hasName);
		
		// - PARAMETER-NAME (1..1)
		node.setProperty(OASIS_RIM_PARAMETER_NAME, parameterName);

		return node;

	}

	public static Object toBinding(Node node) {
		
		ParameterType binding = factory.createParameterType();
		binding = (ParameterType)ExtensibleObjectTypeNEO.fillBinding(node, binding);

		Iterable<Relationship> relationships = null;

		// - DATA-TYPE (1..1)
		binding.setDataType((String)node.getProperty(OASIS_RIM_DATA_TYPE));
		
		// - DEFAULT-VALUE (0..1)
		if (node.hasProperty(OASIS_RIM_DEFAULT_VALUE)) binding.setDefaultValue((String)node.getProperty(OASIS_RIM_DEFAULT_VALUE));
		
		// - DESCRIPTION (0..1)
		relationships = node.getRelationships(RelationTypes.hasDescription);
		if (relationships != null) {
		
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node internationaStringTypeNode = relationship.getEndNode();
				
				InternationalStringType internationalStringType = (InternationalStringType)InternationalStringTypeNEO.toBinding(internationaStringTypeNode);				
				binding.setDescription(internationalStringType);

			}
			
		}

		// - MIN-OCCURS (0..1)
		if (node.hasProperty(OASIS_RIM_MIN_OCCURS)) binding.setMinOccurs((BigInteger)node.getProperty(OASIS_RIM_MIN_OCCURS));
		
		// - MAX-OCCURS (0..1)
		if (node.hasProperty(OASIS_RIM_MAX_OCCURS)) binding.setMaxOccurs((BigInteger)node.getProperty(OASIS_RIM_MAX_OCCURS));
		
		// - NAME (1..1)		
		relationships = node.getRelationships(RelationTypes.hasName);
		if (relationships != null) {
		
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
			
				Relationship relationship = iterator.next();
				Node internationaStringTypeNode = relationship.getEndNode();
				
				InternationalStringType internationalStringType = (InternationalStringType)InternationalStringTypeNEO.toBinding(internationaStringTypeNode);				
				binding.setName(internationalStringType);

			}
			
		}
		
		// - PARAMETER-NAME (1..1)
		binding.setParameterName((String)node.getProperty(OASIS_RIM_PARAMETER_NAME));

		return binding;
		
	}
	
	public static String getNType() {
		return "ParameterType";
	}
}
