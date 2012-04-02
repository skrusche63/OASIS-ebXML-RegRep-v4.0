package de.kp.registry.server.neo4j.write.plugin;

import java.util.Set;

import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;

import de.kp.registry.server.neo4j.domain.exception.CatalogingException;

public interface CatalogerPlugin {

	public Set<RegistryObjectType> catalogObject(Object registryObject) throws CatalogingException;

}
