package de.kp.registry.server.neo4j.domain;

import java.util.HashMap;
import java.util.Map;

import org.oasis.ebxml.registry.bindings.rim.RoleType;

import de.kp.registry.server.neo4j.domain.association.AssociationTypeNEO;
import de.kp.registry.server.neo4j.domain.classification.ClassificationNodeTypeNEO;
import de.kp.registry.server.neo4j.domain.classification.ClassificationSchemeTypeNEO;
import de.kp.registry.server.neo4j.domain.core.CommentTypeNEO;
import de.kp.registry.server.neo4j.domain.core.ExternalLinkTypeNEO;
import de.kp.registry.server.neo4j.domain.core.ExtrinsicObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.core.RegistryObjectTypeNEO;
import de.kp.registry.server.neo4j.domain.core.RegistryPackageTypeNEO;
import de.kp.registry.server.neo4j.domain.event.AuditableEventTypeNEO;
import de.kp.registry.server.neo4j.domain.event.NotificationTypeNEO;
import de.kp.registry.server.neo4j.domain.event.SubscriptionTypeNEO;
import de.kp.registry.server.neo4j.domain.federation.FederationTypeNEO;
import de.kp.registry.server.neo4j.domain.federation.RegistryTypeNEO;
import de.kp.registry.server.neo4j.domain.provenance.OrganizationTypeNEO;
import de.kp.registry.server.neo4j.domain.provenance.PersonTypeNEO;
import de.kp.registry.server.neo4j.domain.query.QueryDefinitionTypeNEO;
import de.kp.registry.server.neo4j.domain.service.ServiceBindingTypeNEO;
import de.kp.registry.server.neo4j.domain.service.ServiceEndpointTypeNEO;
import de.kp.registry.server.neo4j.domain.service.ServiceInterfaceTypeNEO;
import de.kp.registry.server.neo4j.domain.service.ServiceTypeNEO;

public class NEOMapper {

	private Map<String, Class<?>>mapper;
	
	// mapper for externally used registry objects
	public NEOMapper() {
		
		mapper = new HashMap<String, Class<?>>();
		initialize();
	
	}
	
	private void initialize() {
		
		// Access Model
		mapper.put("RoleType", RoleType.class);
		
		// Association Model
		mapper.put("AssociationType", AssociationTypeNEO.class);
		
		// Classification Model
		mapper.put("ClassificationNodeType",   ClassificationNodeTypeNEO.class);
		mapper.put("ClassificationSchemeType", ClassificationSchemeTypeNEO.class);
		
		// Core Model
		mapper.put("CommentType", 		  CommentTypeNEO.class);
		mapper.put("ExternalLinkType", 	  ExternalLinkTypeNEO.class);
		mapper.put("ExtrinsicObjectType", ExtrinsicObjectTypeNEO.class);
		mapper.put("RegistryObjectType",  RegistryObjectTypeNEO.class);
		mapper.put("RegistryPackageType", RegistryPackageTypeNEO.class);
		
		// Event Model
		mapper.put("AuditableEventType", AuditableEventTypeNEO.class);
		mapper.put("NotificationType", 	 NotificationTypeNEO.class);
		mapper.put("SubscriptionType", 	 SubscriptionTypeNEO.class);
		
		// Federation Model
		mapper.put("FederationType", FederationTypeNEO.class);
		mapper.put("RegistryType",   RegistryTypeNEO.class);
		
		// Provenance Model
		mapper.put("OrganizationType", OrganizationTypeNEO.class);
		mapper.put("PersonType", 	   PersonTypeNEO.class);

		// Query Model
		mapper.put("QueryDefinitionType", QueryDefinitionTypeNEO.class);

		// Service Model
		mapper.put("ServiceBindingType",   ServiceBindingTypeNEO.class);
		mapper.put("ServiceEndpointType",  ServiceEndpointTypeNEO.class);
		mapper.put("ServiceInterfaceType", ServiceInterfaceTypeNEO.class);
		mapper.put("ServiceType", 		   ServiceTypeNEO.class);
		
	}
	
	public Class<?> get(String clazzName) {
		
		System.out.println(clazzName);
		return mapper.get(clazzName);
	}
}
