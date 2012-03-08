package de.kp.registry.server.neo4j.domain.util;

import java.util.List;

import org.oasis.ebxml.registry.bindings.lcm.UpdateActionType;

// this is a helper class to wrap a List<UpdateActionType>
// for java reflection purpose

public class UpdateActionListType {

	private List<UpdateActionType> list;
	
	public UpdateActionListType(List<UpdateActionType> list) {
		this.list = list;
	}
	
	public List<UpdateActionType> getUpdateAction() {
		return list;
	}
}
