package de.kp.registry.server.neo4j.authorization.xacml;

public class PolicyDecisionPoint {

	private static PolicyDecisionPoint instance = new PolicyDecisionPoint();
	
	private PolicyDecisionPoint() {
	}
	
	public static PolicyDecisionPoint getInstance() {
		if (instance == null) instance = new PolicyDecisionPoint();
		return instance;
	}
	
	public void init() {
		// TODO
	}
	
	public void evaluate() {
		// TODO
	}
}
