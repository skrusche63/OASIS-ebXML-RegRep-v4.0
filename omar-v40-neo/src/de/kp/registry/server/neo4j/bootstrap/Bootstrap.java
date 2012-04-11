package de.kp.registry.server.neo4j.bootstrap;

import java.lang.reflect.Method;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import de.kp.registry.server.neo4j.domain.NEOBase;

public class Bootstrap {

	public Node toNode(EmbeddedGraphDatabase graphDB, Object binding, Boolean checkReference) throws Exception {

		Class<?> clazz = NEOBase.getClassNEO(binding);

	    Method method = clazz.getMethod("toNode", graphDB.getClass(), Object.class, Boolean.class);
	    return (Node) method.invoke(null, graphDB, binding, checkReference);
    	
	}
}
