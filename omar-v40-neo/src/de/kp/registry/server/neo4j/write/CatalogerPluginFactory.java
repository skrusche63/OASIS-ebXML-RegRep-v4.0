package de.kp.registry.server.neo4j.write;

import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.server.neo4j.write.plugin.CatalogerPlugin;
import de.kp.registry.server.neo4j.write.plugin.WSDLCatalogerPlugin;

public class CatalogerPluginFactory {

	private static CatalogerPluginFactory instance = new CatalogerPluginFactory();
	
	private CatalogerPluginFactory() {
	}
	
	public static CatalogerPluginFactory getInstance() {
		if (instance == null) instance = new CatalogerPluginFactory();
		return instance;
	}
	
	public CatalogerPlugin getCatalogerPlugin(String objectType) {
		
		if (CanonicalConstants.CANONICAL_OBJECT_TYPE_ID_WSDL.equals(objectType))
			return new WSDLCatalogerPlugin();
		
		return null;
	}
	
}
