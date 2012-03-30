package de.kp.registry.server.neo4j.write;

public class CatalogerPluginFactory {

	private static CatalogerPluginFactory instance = new CatalogerPluginFactory();
	
	private CatalogerPluginFactory() {
	}
	
	public static CatalogerPluginFactory getInstance() {
		if (instance == null) instance = new CatalogerPluginFactory();
		return instance;
	}
	
	public CatalogerPlugin getCatalogerPlugin(String objectType) {
		return null;
	}
	
}
