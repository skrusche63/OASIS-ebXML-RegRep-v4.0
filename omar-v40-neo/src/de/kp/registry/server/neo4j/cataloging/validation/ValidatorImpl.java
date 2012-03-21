package de.kp.registry.server.neo4j.cataloging.validation;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.oasis.ebxml.registry.bindings.spi.ValidateObjectsRequest;
import org.oasis.ebxml.registry.bindings.spi.ValidateObjectsResponse;

import de.kp.registry.server.neo4j.service.MsgRegistryException;
import de.kp.registry.server.neo4j.service.Validator;

@WebService(name = "Validator", serviceName = "Validator", portName = "ValidatorPort", targetNamespace = "urn:oasis:names:tc:ebxml-regrep:wsdl:registry:services:4.0",
endpointInterface = "de.kp.registry.server.neo4j.service.Validator")

@HandlerChain(file="handler-chain.xml")

public class ValidatorImpl implements Validator {

	@Resource 
	WebServiceContext wsContext;

	public ValidatorImpl() {
	}
	
	public ValidateObjectsResponse validateObjects(ValidateObjectsRequest request) throws MsgRegistryException {
		// TODO
		return null;
	}

}
