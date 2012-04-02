package de.kp.registry.server.neo4j.write.plugin;


/*
 * This class is an adapted version of WSDLConstants from OMAR v3.1; 
 * the original code is subject to the freebxml License, Version 1.1
 * 
 * @author psterk
 * @author Dr. Stefan Krusche (krusche@dr-kruscheundpartner.de)
 * 
 */

import javax.xml.namespace.QName;

/**
 * WSDL-related constants.
 */
public interface WSDLConstants {

    // namespace URIs
    public final static String NS_XMLNS = "http://www.w3.org/2000/xmlns/";
    public final static String NS_WSDL = "http://schemas.xmlsoap.org/wsdl/";
    public final static String NS_WSDL_SOAP = "http://schemas.xmlsoap.org/wsdl/soap/";
    public final static String NS_XSD ="http://www.w3.org/2001/XMLSchema";
    
    // prefixes
    public final static String PREFIX_XMLNS = "xmlns";
    
    // QNames
    public final static QName QNAME_BINDING = new QName(NS_WSDL, "binding");
    public final static QName QNAME_DEFINITIONS = new QName(NS_WSDL, "definitions");
    public final static QName QNAME_DOCUMENTATION = new QName(NS_WSDL, "documentation");
    public final static QName QNAME_FAULT = new QName(NS_WSDL, "fault");
    public final static QName QNAME_IMPORT = new QName(NS_WSDL, "import");
    public final static QName QNAME_INPUT = new QName(NS_WSDL, "input");
    public final static QName QNAME_MESSAGE = new QName(NS_WSDL, "message");
    public final static QName QNAME_OPERATION = new QName(NS_WSDL, "operation");
    public final static QName QNAME_OUTPUT = new QName(NS_WSDL, "output");
    public final static QName QNAME_PART = new QName(NS_WSDL, "part");
    public final static QName QNAME_PORT = new QName(NS_WSDL, "port");
    public final static QName QNAME_PORT_TYPE = new QName(NS_WSDL, "portType");
    public final static QName QNAME_SERVICE = new QName(NS_WSDL, "service");
    public final static QName QNAME_TYPES = new QName(NS_WSDL, "types");
    public final static QName QNAME_ATTR_ARRAY_TYPE = new QName(NS_WSDL, "arrayType");
    
    public final static QName QNAME_SOAP_ADDRESS = new QName(NS_WSDL_SOAP, "address");
    public final static QName QNAME_SOAP_BINDING = new QName(NS_WSDL_SOAP, "binding");
    public final static QName QNAME_SOAP_BODY = new QName(NS_WSDL_SOAP, "body");
    public final static QName QNAME_SOAP_OPERATION = new QName(NS_WSDL_SOAP, "operation");
    
    public final static QName QNAME_XSD_SCHEMA = new QName(NS_XSD, "schema");
    public final static QName QNAME_XSD_IMPORT = new QName(NS_XSD, "import");
    public final static QName QNAME_XSD_REDEFINE = new QName(NS_XSD, "redefine");
    public final static QName QNAME_XSD_INCLUDE = new QName(NS_XSD, "include");
    
    public final static String URI_SOAP_TRANSPORT_HTTP =
        "http://schemas.xmlsoap.org/soap/http";
    public final static String RPC = "rpc";
    public final static String DOCUMENT = "document";
    
        // WSDL attribute names
    public final static String ATTR_NAME = "name";
    public final static String ATTR_TYPE = "type";
    public final static String ATTR_BINDING = "binding";
    public final static String ATTR_LOCATION = "location";
    public final static String ATTR_TRANSPORT = "transport";
    public final static String ATTR_STYLE = "style";
    public final static String ATTR_NAMESPACE = "namespace";
}