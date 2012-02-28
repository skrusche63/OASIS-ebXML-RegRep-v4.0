package de.kp.registry.server.neo4j.domain.query;

import java.math.BigInteger;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.oasis.ebxml.registry.bindings.rim.InternationalStringType;
import org.oasis.ebxml.registry.bindings.rim.ParameterType;

import de.kp.registry.server.neo4j.domain.RelationTypes;
import de.kp.registry.server.neo4j.domain.core.ExtensibleObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.core.InternationalStringTypeNEO;

public class ParameterTypeNEO extends ExtensibleObjectTypeNEO {

	public static Node toNode(EmbeddedGraphDatabase graphDB, Object binding) throws Exception {
		
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
		
		// create node from underlying ExtensibleObjectType
		Node parameterTypeNode = ExtensibleObjectTypeNEO.toNode(graphDB, binding);
		
		// update the internal type to describe a ParameterType
		parameterTypeNode.setProperty(NEO4J_TYPE, getNType());

		// - DATA-TYPE (1..1)
		parameterTypeNode.setProperty(OASIS_RIM_DATA_TYPE, dataType);	
		
		// - DEFAULT-VALUE (0..1)
		if (defaultValue != null) parameterTypeNode.setProperty(OASIS_RIM_DEFAULT_VALUE, defaultValue);
		
		// - DESCRIPTION (0..1)
		if (description != null) {
			
			Node internationalStringTypeNode = InternationalStringTypeNEO.toNode(graphDB, description);
			parameterTypeNode.createRelationshipTo(internationalStringTypeNode, RelationTypes.hasDescription);

		}

		// - MIN-OCCURS (0..1)
		if (minOccurs != null) parameterTypeNode.setProperty(OASIS_RIM_MIN_OCCURS, minOccurs);
		
		// - MAX-OCCURS (0..1)
		if (maxOccurs != null) parameterTypeNode.setProperty(OASIS_RIM_MAX_OCCURS, maxOccurs);
		
		// - NAME (1..1)		
		Node internationalStringTypeNode = InternationalStringTypeNEO.toNode(graphDB, name);
		parameterTypeNode.createRelationshipTo(internationalStringTypeNode, RelationTypes.hasName);
		
		// - PARAMETER-NAME (1..1)
		parameterTypeNode.setProperty(OASIS_RIM_PARAMETER_NAME, parameterName);

		return parameterTypeNode;
		
	}

	public static String getNType() {
		return "ParameterType";
	}
}
