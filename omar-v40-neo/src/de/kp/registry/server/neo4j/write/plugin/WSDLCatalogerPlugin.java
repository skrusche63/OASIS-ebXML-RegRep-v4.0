package de.kp.registry.server.neo4j.write.plugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.namespace.QName;

import org.oasis.ebxml.registry.bindings.rim.AssociationType;
import org.oasis.ebxml.registry.bindings.rim.ClassificationType;
import org.oasis.ebxml.registry.bindings.rim.CollectionValueType;
import org.oasis.ebxml.registry.bindings.rim.ExternalLinkType;
import org.oasis.ebxml.registry.bindings.rim.ExtrinsicObjectType;
import org.oasis.ebxml.registry.bindings.rim.InternationalStringType;
import org.oasis.ebxml.registry.bindings.rim.LocalizedStringType;
import org.oasis.ebxml.registry.bindings.rim.RegistryObjectType;
import org.oasis.ebxml.registry.bindings.rim.ServiceEndpointType;
import org.oasis.ebxml.registry.bindings.rim.ServiceType;
import org.oasis.ebxml.registry.bindings.rim.SimpleLinkType;
import org.oasis.ebxml.registry.bindings.rim.SlotType;
import org.oasis.ebxml.registry.bindings.rim.StringValueType;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.common.CanonicalSchemes;
import de.kp.registry.common.RIMFactory;
import de.kp.registry.common.URN;
import de.kp.registry.server.neo4j.domain.exception.CatalogingException;
import de.kp.registry.server.neo4j.read.ReadManager;
import de.kp.registry.server.neo4j.util.FileUtil;
import de.kp.registry.server.neo4j.util.XMLUtil;

public class WSDLCatalogerPlugin extends CatalogerPluginImpl {

    public static final String TMP_DIR = System.getProperty("java.io.tmpdir");

    private WSDLDocument wsdlDocument;    
    private Map<String, File> fileMap;
    
    // HashMap where key is id and value is the RIM object representing a WSDL object 
    HashMap<String, RegistryObjectType> idToRIMMap = new HashMap<String, RegistryObjectType>();
    
    // HashMap where key is id and value is the WSDL object 
    HashMap<String, Element> idToWSDLMap = new HashMap<String, Element>();

    // return set of registry objects
    private Set<RegistryObjectType> registryObjects;

	// reference to OASIS ebRIM object factory
	private static org.oasis.ebxml.registry.bindings.rim.ObjectFactory ebRIMFactory = new org.oasis.ebxml.registry.bindings.rim.ObjectFactory();
    
	
	// This class catalogues a certain WSDL document, which is
	// the repository item of a certain extrinsic object type
	
	public WSDLCatalogerPlugin() {
		
		fileMap 		= new HashMap<String, File>();
		registryObjects = new HashSet<RegistryObjectType>();
		
	}
	
	// this method serves as a dispatcher that distinguishes
	// external links and extrinsic objects
	
	public Set<RegistryObjectType> catalogObject(Object registryObject) throws CatalogingException {

		if (registryObject instanceof ExtrinsicObjectType) {
			
			ExtrinsicObjectType extrinsicObject = (ExtrinsicObjectType)registryObject;
			DataHandler repositoryItem = extrinsicObject.getRepositoryItem();

            try {

            	InputSource inputSource = new InputSource(repositoryItem.getInputStream());
	            if (extrinsicObject.getMimeType().equalsIgnoreCase("text/xml")) {
	                catalogWSDLObject(extrinsicObject, inputSource, true);
	            
	            } else {
	                catalogWSDLExtrinsicObject(extrinsicObject, inputSource);
	            }

            } catch (IOException e) {
				e.printStackTrace();
			}

		} else if (registryObject instanceof ExternalLinkType) {

            ExternalLinkType externalLink = (ExternalLinkType)registryObject;
            SimpleLinkType externalRef = externalLink.getExternalRef();
            
            String wsdlLocation = getWSDLLocation(externalRef);
            InputSource inputSource = new InputSource(wsdlLocation);
            
            catalogWSDLExternalLink(externalLink, inputSource);

		} else {
			throw new CatalogingException("[WSDLCatalogerPlugin] Neither ExternalLinkType nor ExtrinsicObjectType found.");
			
		}
 
		return registryObjects;
		
	}

    private void catalogWSDLObject(RegistryObjectType registryObject, InputSource inputSource) throws CatalogingException {
    	catalogWSDLObject(registryObject, inputSource, false);
    }

	private void catalogWSDLObject(RegistryObjectType registryObject, InputSource inputSource, boolean standAlone) throws CatalogingException {
		
		if (standAlone == false) registryObjects.add(registryObject);

		try {
			wsdlDocument = new WSDLDocument(inputSource);
		
		} catch (CatalogingException ce) {
			throw ce;

		} catch (Throwable t) {
			throw new CatalogingException("[WSDLCatalogerPlugin] " + t.getMessage());
		
		}
		
		addImportedNamespacesSlot(registryObject);
		catalogTargetNamespace(registryObject);

		List<Element> serviceElements = wsdlDocument.getElements(WSDLConstants.QNAME_SERVICE);
		Iterator<Element> serviceElementItr = serviceElements.iterator();

		while (serviceElementItr.hasNext()) {

			Element serviceElement = (Element)serviceElementItr.next();

			// Pass registry object to this method so as to create assocation of type Imports
			processService(serviceElement);

			// Set the id for the WSDL EO
			updateRegistryObjectId(wsdlDocument.getTargetNamespaceURI(), registryObject, standAlone);

			// Create a Contains association between the WSDL EO 
			// and the wsdl:service contained therein
			createAssociation_Contains(registryObject, getServiceId(serviceElement));

		}

		List<Element> bindings = wsdlDocument.getElements(WSDLConstants.QNAME_BINDING);
		Iterator<Element> bindingElementItr = bindings.iterator();

		while (bindingElementItr.hasNext()) {

			Element bindingElement = (Element)bindingElementItr.next();

			// Pass regisry object to this method so as to create assocation of type Imports
			processBinding(bindingElement);

			// Set the id for the WSDL EO
			updateRegistryObjectId(wsdlDocument.getTargetNamespaceURI(), registryObject, standAlone);

			// Create a Contains association between the WSDL EO 
			// and the wsdl:binding contained therein
			createAssociation_Contains(registryObject, getBindingId(bindingElement));
		
		}

		List<Element> portTypes = wsdlDocument.getElements(WSDLConstants.QNAME_PORT_TYPE);
		Iterator<Element> portTypeElementItr = portTypes.iterator();

		while (portTypeElementItr.hasNext()) {

			Element portTypeElement = (Element)portTypeElementItr.next();
			processPortType(portTypeElement);

			// Set the id for the WSDL EO
			updateRegistryObjectId(wsdlDocument.getTargetNamespaceURI(), registryObject, standAlone);

			// Need to create a Contains association between the WSDL EO 
			// and the wsdl:portType contained therein
			createAssociation_Contains(registryObject, getPortTypeId(portTypeElement));

			// Determine if this WSDL file imports other WSDL files
			// catalogImportStatements(wsdlRO, portType);
			// Resolve any <types> imports
			resolveTypeImports(portTypeElement, registryObject); 
		
		}
		
		catalogImportStatements(registryObject, wsdlDocument);

	}          

	
    private void catalogWSDLExtrinsicObject(ExtrinsicObjectType extrinsicObject, InputSource inputSource) throws CatalogingException {

    	try {

    		if (extrinsicObject.getMimeType().equalsIgnoreCase("application/zip")) {
    			catalogZipFile(extrinsicObject, inputSource);

    		} else {	
    			catalogWSDLObject(extrinsicObject, inputSource);

    		} 
    		
    	} catch (CatalogingException e) {
    		throw e;
    	
    	} catch (Throwable e) {
    		throw new CatalogingException("[WSDLCatalogerPlugin] " + e.getMessage());
    	}

    }
	
	private void catalogWSDLExternalLink(ExternalLinkType externalLink, InputSource inputSource) throws CatalogingException {
		catalogWSDLObject(externalLink, inputSource, true);
	}

	private void catalogXMLSExtrinsicObject(ExtrinsicObjectType extrinsicObject, InputSource inputSource) throws CatalogingException {

		try { 

			// add to response set
			registryObjects.add(extrinsicObject);
			
			Document document 	  = XMLUtil.parse(inputSource);
            Element schemaElement = document.getDocumentElement();
 
            String documentLocalName 	= schemaElement.getLocalName();
            String documentNamespaceURI = schemaElement.getNamespaceURI();
 
            if (documentLocalName.equalsIgnoreCase("schema") && documentNamespaceURI.endsWith("XMLSchema")) {
                
            	Attr attribute = schemaElement.getAttributeNode("targetNamespace");
                String namespaceURI = attribute.getValue();

                // set the id for the XMLSchema extrinsic object
                updateRegistryObjectId(namespaceURI, extrinsicObject, false);
                
                // Check if this XSD file imports another file (usually XSD)
                NodeList nodeList = schemaElement.getChildNodes();
                
                int length = nodeList.getLength();
                for (int i = 0; i < length; i++) {
                    
                	Node node = nodeList.item(i);
                    
                	String localName = node.getLocalName();
                    if (localName != null && localName.equalsIgnoreCase("import")) {
                        
                    	// This XSD imports another file
                        NamedNodeMap importNamedNodeMap = node.getAttributes();
                        Node namespaceNode = importNamedNodeMap.getNamedItem("namespace");
                        
                        String importNamespace = null;
                        if (namespaceNode != null) importNamespace = namespaceNode.getNodeValue();

                        String schemaLocation = null;
                        
                        Node schemaLocationNode = importNamedNodeMap.getNamedItem("schemaLocation");
                        if (schemaLocationNode != null) schemaLocation = schemaLocationNode.getNodeValue();

                        RegistryObjectType importedObject = catalogImportStatement(extrinsicObject, importNamespace, schemaLocation);
                        createAssociation_Imports(extrinsicObject, importedObject);
                    
                    }
                }
            }

		// } catch (CatalogingException e) {
        //    throw e;
        
		} catch (Exception e) {
    		throw new CatalogingException("[WSDLCatalogerPlugin] " + e.getMessage());

		}

	}
	
	/************************************************************************
	 * 
	 * HELPER    HELPER    HELPER    HELPER    HELPER    HELPER    HELPER
	 * 
	 ***********************************************************************/
	
	private String getWSDLLocation(SimpleLinkType externalRef) {
		/*
		 * String wsdlLoc = Utility.absolutize(Utility.getFileOrURLName(urlStr));
		 */
		
		return null;
	}
	
    private void catalogZipFile(ExtrinsicObjectType extrinsicObject, InputSource inputSource) throws CatalogingException {
  
 		try {

 			// this method unzips the zip file and places all files in the fileMap
	        Collection<File> files = fillFileMap(extrinsicObject, inputSource);

	        // now iterate and create ExtrinsicObject - Repository Item pair for each unzipped file
	        Iterator<File> iter = files.iterator();
	        
	        while (iter.hasNext()) {
	                
	        	File file = (File)iter.next();
	            String fileNameAbsolute = file.getAbsolutePath();
	                
	        	String tmp_dir = TMP_DIR;
	            if(!tmp_dir.endsWith(File.separator)) tmp_dir = tmp_dir+File.separator;
	            
	            String fileNameRelative = fileNameAbsolute.substring(tmp_dir.length(), fileNameAbsolute.length());    
	            ExtrinsicObjectType derivedExtrinsicObject = createExtrinsicObject(fileNameRelative);
	            
	            derivedExtrinsicObject.setRepositoryItem(new DataHandler(new FileDataSource(file)));
	            
	            // Create the InputSource
	            String url = FileUtil.absolutize(FileUtil.getFileOrURLName(fileNameAbsolute));
	            inputSource = new InputSource(url);
	                
	            if (derivedExtrinsicObject.getObjectType().equals(CanonicalConstants.CANONICAL_OBJECT_TYPE_ID_WSDL)) {
	                catalogWSDLExtrinsicObject(derivedExtrinsicObject, inputSource);
	                
	            } else if (derivedExtrinsicObject.getObjectType().equals(CanonicalConstants.CANONICAL_OBJECT_TYPE_ID_XMLSchema)) {
	                catalogXMLSExtrinsicObject(derivedExtrinsicObject, inputSource);
	            }
	 
	        }
	        	        
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    
    }

    private ExtrinsicObjectType createExtrinsicObject(String fileRelativeName) throws CatalogingException {
 
    	ExtrinsicObjectType extrinsicObject = null;
    	
        try {
        
        	String absoluteFileName = FileUtil.getCompleteRelativeFileName(fileRelativeName);
            extrinsicObject = (ExtrinsicObjectType)getRegistryObject(absoluteFileName);
            
            if (extrinsicObject == null) {
 
            	// Get the filename
                File file = (File)fileMap.get(absoluteFileName);
                if (file != null) {

                	// Create the ExtrinsicObjectType instance
                    extrinsicObject = ebRIMFactory.createExtrinsicObjectType();

                    // Add wsdl:Service namespace to this Id later during parsing
                    // of WSDL file
                    extrinsicObject.setId(absoluteFileName);
                    String fileName = file.getName();

                    // Add fileName as name of eo
                    LocalizedStringType nameType = ebRIMFactory.createLocalizedStringType();
                    
                    nameType.setLang(CanonicalConstants.DEFAULT_LANGUAGE);
                    nameType.setValue(fileName);
                    
                    InternationalStringType internationalString = ebRIMFactory.createInternationalStringType();
                    internationalString.getLocalizedString().add(nameType);
                    
                    extrinsicObject.setName(internationalString);
                    
                    // Add metadata
                    setObjectAndMimeType(extrinsicObject, fileName);
                    assignURL(extrinsicObject, absoluteFileName);

                    idToRIMMap.put(absoluteFileName, extrinsicObject);                
                
                }
            }

        } catch (Throwable t) {
            throw new CatalogingException("[WSDLCatalogerPlugin] " + t.getMessage());
        }
     
        return extrinsicObject;
    }
    
    private Collection<File> fillFileMap(ExtrinsicObjectType zipEO, InputSource inputSource) throws IOException {
            
    	// unzip the files in tmp dir
        ArrayList<File> files = FileUtil.unzip(TMP_DIR, inputSource.getByteStream());
            
        Iterator<File> iter = files.iterator();
        while (iter.hasNext()) {
                
        	File file = (File)iter.next();
        	
            String fileNameAbsolute = file.getAbsolutePath();
            String fileNameRelative = fileNameAbsolute.substring(TMP_DIR.length()+1, fileNameAbsolute.length());
                
            fileMap.put(fileNameRelative, file);
        
        }
            
        return fileMap.values();
    
    }

    
    private void createAssociation_Contains(RegistryObjectType registryObject, String rimId) throws CatalogingException {
        
    	try {         
        
    		String sid = registryObject.getId();
    		String tid = rimId;
    				
            AssociationType associationType = ebRIMFactory.createAssociationType();
    
            associationType.setSourceObject(sid);
            associationType.setTargetObject(tid);

            associationType.setType(CanonicalSchemes.CANONICAL_ASSOCIATION_TYPE_ID_Contains);
            associationType.setId(sid + ":Contains:" + tid);

            registryObjects.add(associationType);
 
    	} catch (Throwable t) {
            throw new CatalogingException("[WSDLCatalogerPlugin] " + t.getMessage());

    	}
    
    }

    private void createAssociation_Implements(RegistryObjectType sourceObject, RegistryObjectType targetObject) throws CatalogingException {
        
    	try {         
        
    		String sid = sourceObject.getId();
    		String tid = targetObject.getId();
    				
            AssociationType associationType = ebRIMFactory.createAssociationType();
    
            associationType.setSourceObject(sid);
            associationType.setTargetObject(tid);

            associationType.setType(CanonicalSchemes.CANONICAL_ASSOCIATION_TYPE_ID_Implements);
            associationType.setId(sid + ":Implements:" + tid);

            registryObjects.add(associationType);
 
    	} catch (Throwable t) {
            throw new CatalogingException("[WSDLCatalogerPlugin] " + t.getMessage());

    	}
    
    }

    private void createAssociation_Imports(RegistryObjectType registryObject, RegistryObjectType importedRegistryObject) throws CatalogingException {
        
    	if ((registryObject != null) && (importedRegistryObject != null)) {
    		
            try {
            	
            	String sid = registryObject.getId();
            	String tid = importedRegistryObject.getId();
            	
                AssociationType associationType = ebRIMFactory.createAssociationType();
                
                associationType.setSourceObject(sid);
                associationType.setTargetObject(tid);
                
                associationType.setType(CanonicalSchemes.CANONICAL_ASSOCIATION_TYPE_ID_Imports);
                associationType.setId(sid + ":Imports:" + tid);

                registryObjects.add(associationType);

            } catch (Throwable t) {
                throw new CatalogingException("[WSDLCatalogerPlugin] " + t.getMessage());
            }
        
    	}
    }

    private String getServiceId(Element service) throws CatalogingException {
        
    	String targetNamespace = wsdlDocument.getTargetNamespaceURI(service, WSDLConstants.QNAME_SERVICE);
        String nameStr = wsdlDocument.getAttribute(service, WSDLConstants.ATTR_NAME);
        
        String id = XMLUtil.fixURN(targetNamespace + ":service:" + nameStr);
        return id;
    
    }
    
    private String getPortId(Element port) throws CatalogingException {
    
    	String targetNamespace = wsdlDocument.getTargetNamespaceURI(port, WSDLConstants.QNAME_PORT);
        String nameStr = wsdlDocument.getAttribute(port, WSDLConstants.ATTR_NAME);
        
        String id = XMLUtil.fixURN(targetNamespace + ":port:" + nameStr);
        return id;
    
    }
   
    private String getBindingId(Element binding) throws CatalogingException {
        
    	String targetNamespace = wsdlDocument.getTargetNamespaceURI(binding, WSDLConstants.QNAME_BINDING);
        String nameStr = wsdlDocument.getAttribute(binding, WSDLConstants.ATTR_NAME);
        
        String id = XMLUtil.fixURN(targetNamespace + ":binding:" + nameStr);
        return id;
    
    }
    
    private String getPortTypeId(Element portType) throws CatalogingException {
    
    	String targetNamespace = wsdlDocument.getTargetNamespaceURI(portType, WSDLConstants.QNAME_PORT_TYPE);
        String nameStr = wsdlDocument.getAttribute(portType, WSDLConstants.ATTR_NAME);
        
        String id = XMLUtil.fixURN(targetNamespace + ":portType:" + nameStr);
        return id;
    
    }
    

    private List<Element> resolveTypes(WSDLDocument wsdlDocument) throws CatalogingException {
        return wsdlDocument.getElements(WSDLConstants.QNAME_TYPES);
    }
    
    private void addImportedNamespacesSlot(RegistryObjectType registryObject) throws CatalogingException {

    	try {
            
    		SlotType slotType = ebRIMFactory.createSlotType();
            
    		slotType.setName(CanonicalConstants.CANONICAL_SLOT_WSDL_PROFILE_REFERENCED_NAMESPACES);
            slotType.setType(CanonicalSchemes.CANONICAL_DATA_TYPE_ID_String);
            
            // document get all namespaces
            Collection<String> nameSpaceURIs = wsdlDocument.getAllNamespaceURIs();
            CollectionValueType collection = ebRIMFactory.createCollectionValueType();
            
            Iterator<String> iter = nameSpaceURIs.iterator();
            while (iter.hasNext()) {
            	
            	StringValueType value = ebRIMFactory.createStringValueType();
            	value.setValue(iter.next());
            	
            	collection.getElement().add(value);
            	
            }
            
            slotType.setSlotValue(collection);
            registryObject.getSlot().add(slotType);
        
    	} catch (Exception e) {
            throw new CatalogingException("[WSDLCatalogerPlugin] " + e.getMessage());

    	}
    
    }
    
    private void catalogTargetNamespace(RegistryObjectType registryObject) throws CatalogingException {
        
    	try {
            
    		SlotType slotType = ebRIMFactory.createSlotType();
    		
            slotType.setName(CanonicalConstants.CANONICAL_SLOT_WSDL_PROFILE_TARGET_NAMESPACE);
            slotType.setType(CanonicalSchemes.CANONICAL_DATA_TYPE_ID_String);
            
            String targetNamespace = wsdlDocument.getTargetNamespaceURI();
 
        	StringValueType value = ebRIMFactory.createStringValueType();
        	value.setValue(targetNamespace);
        	
        	slotType.setSlotValue(value);
            registryObject.getSlot().add(slotType);
        
    	} catch (Exception e) {
            throw new CatalogingException("[WSDLCatalogerPlugin] " + e.getMessage());

        }
    }
    
    private RegistryObjectType getRegistryObject(String id) {
        return (RegistryObjectType)idToRIMMap.get(id);
    }

    private void assignURL(ExtrinsicObjectType extrinsicObject, String urlSuffix) {
    	
    	SlotType slotType = ebRIMFactory.createSlotType();
    	
    	slotType.setName(CanonicalConstants.CANONICAL_SLOT_CONTENT_LOCATOR);
    	slotType.setType(CanonicalSchemes.CANONICAL_DATA_TYPE_ID_String);
    	
    	StringValueType value = ebRIMFactory.createStringValueType();
    	value.setValue(urlSuffix);
    	
    	extrinsicObject.getSlot().add(slotType);

    }

    private void processService(Element serviceElement) throws CatalogingException {
  
    	try { 
            
    		String id = getServiceId(serviceElement);            
            if (idToWSDLMap.containsKey(id)) return;

            // Create a RIM Service instances for service tag 
            ServiceType serviceType = ebRIMFactory.createServiceType();
                       
            idToRIMMap.put(id, serviceType);
            idToWSDLMap.put(id, serviceElement);
            
            // Set id
            serviceType.setId(id);
            
            InternationalStringType internationalString = null;

            // Set name
            LocalizedStringType nameType = ebRIMFactory.createLocalizedStringType();
            
            nameType.setLang(CanonicalConstants.DEFAULT_LANGUAGE);
            nameType.setValue(wsdlDocument.getAttribute(serviceElement, WSDLConstants.ATTR_NAME));
            
            internationalString = ebRIMFactory.createInternationalStringType();
            internationalString.getLocalizedString().add(nameType);
            
            serviceType.setName(internationalString);
            
            // Set description
            Element docElement = wsdlDocument.getElement(serviceElement, WSDLConstants.QNAME_DOCUMENTATION);
            if (docElement != null) {
            	
                LocalizedStringType descriptionType = ebRIMFactory.createLocalizedStringType();
                
                descriptionType.setLang(CanonicalConstants.DEFAULT_LANGUAGE);
                descriptionType.setValue(docElement.getFirstChild().getNodeValue());
                
                internationalString = ebRIMFactory.createInternationalStringType();
                internationalString.getLocalizedString().add(descriptionType);

            	
                serviceType.setDescription(internationalString);
            
            }
            
            catalogTargetNamespace(serviceType);
            
            // Add wsdl Service Classification
            ClassificationType classificationType = RIMFactory.createClassification();
            
            classificationType.setClassifiedObject(serviceType.getId());
            classificationType.setClassificationNode(CanonicalConstants.CANONICAL_OBJECT_TYPE_ID_WSDL_SERVICE);           
            
            serviceType.getClassification().add(classificationType);
            
            // Now process the Port instances for the Service           
            List<Element> portElements = wsdlDocument.getRequiredElements(serviceElement, WSDLConstants.QNAME_PORT);
            Iterator<Element> portElementItr = portElements.iterator();
            
            while (portElementItr.hasNext()) {
                
            	Element portElement = (Element)portElementItr.next();
            	
            	// a port is profiled as a service endpoint and
            	// associated with the respective service
                processPort(portElement);
            
            }                    
        
    	} catch (Exception e) {
            throw new CatalogingException("[WSDLCatalogerPlugin] " + e.getMessage());
        
        }        
    
    }

    private void updateRegistryObjectId(String namespaceURI, RegistryObjectType registryObject, boolean standAlone) throws CatalogingException {

    	String oid = registryObject.getId();
    	if (! isURNValid(oid)) {

    		String localPartEOId = XMLUtil.getLocalPart(oid); 
    		String qualifiedId = getQualifiedId(namespaceURI, localPartEOId);

    		registryObject.setId(qualifiedId);

    		// Do not persist standalone ROs - they have been previously 
    		// persisted by the LCM

    		if (standAlone == false) {
    			
    			idToRIMMap.remove(oid);
    			idToRIMMap.put(qualifiedId, registryObject);

    		}
    	}
    }

            
    private void processPort(Element portElement) throws CatalogingException {
    	
        try {
            
        	String id = getPortId(portElement);            
            if (idToWSDLMap.containsKey(id))return;
            
            // Create a RIM ServiceEndpoint for service tag 
            ServiceEndpointType serviceEndpoint = ebRIMFactory.createServiceEndpointType();
             
            //Set id
            serviceEndpoint.setId(id);
            
            idToRIMMap.put(id, serviceEndpoint);
            idToWSDLMap.put(id, portElement);
            
            InternationalStringType internationalString = null;
            
            //Set name
            LocalizedStringType nameType = ebRIMFactory.createLocalizedStringType();
            
            nameType.setLang(CanonicalConstants.DEFAULT_LANGUAGE);
            nameType.setValue(wsdlDocument.getAttribute(portElement, WSDLConstants.ATTR_NAME));
            
            internationalString = ebRIMFactory.createInternationalStringType();
            internationalString.getLocalizedString().add(nameType);
            
            serviceEndpoint.setName(internationalString);
            
            //Set description
            Element docElement = wsdlDocument.getElement(portElement,  WSDLConstants.QNAME_DOCUMENTATION);
            if (docElement != null) {
                 
                LocalizedStringType descriptionType = ebRIMFactory.createLocalizedStringType();
                
                descriptionType.setLang(CanonicalConstants.DEFAULT_LANGUAGE);
                descriptionType.setValue(docElement.getFirstChild().getNodeValue());
                
                internationalString = ebRIMFactory.createInternationalStringType();
                internationalString.getLocalizedString().add(descriptionType);
               
                serviceEndpoint.setDescription(internationalString);

            }
            
            catalogTargetNamespace(serviceEndpoint);
            
            // Add wsdl Service Classification
            createClassificationType(serviceEndpoint, CanonicalConstants.CANONICAL_OBJECT_TYPE_ID_WSDL_PORT);            

            // Set parent link
            Element serviceElement = (Element)portElement.getParentNode();
            ServiceType rimService = (ServiceType)idToRIMMap.get(getServiceId(serviceElement));

            if (rimService != null) rimService.getServiceEndpoint().add(serviceEndpoint);
           
            // Add accessURI
            String accessURI = null;
            
            Element soapAddressElement = wsdlDocument.getElement(portElement, WSDLConstants.QNAME_SOAP_ADDRESS);
            if (soapAddressElement != null) {

            	accessURI = wsdlDocument.getAttribute(soapAddressElement, WSDLConstants.ATTR_LOCATION);
                if (accessURI != null) serviceEndpoint.setAddress(accessURI);
 
            }
            
            // document get binding
            String bindingStr = wsdlDocument.getAttribute(portElement, WSDLConstants.ATTR_BINDING);
            Element bindingElement = resolveBinding(wsdlDocument, bindingStr);
            
            if (bindingElement == null) throw new CatalogingException("[WSDLCatalogerPlugin] Missing required element.");

            // Now process the Binding instances for the Service
            // Note it may be in an imported document
            processBinding(bindingElement);

            // Set Implements Association between Port and Binding
            ExtrinsicObjectType rimBinding = (ExtrinsicObjectType)idToRIMMap.get(getBindingId(bindingElement));
            createAssociation_Implements(serviceEndpoint, rimBinding);
         
        } catch (CatalogingException ce) {
            throw ce;
        
        } catch (Throwable e) {
            throw new CatalogingException("[WSDLCatalogerPlugin] " + e.getMessage());
            
        }
    
    }

    private void setObjectAndMimeType(RegistryObjectType extrinsicObject, String fileName) throws CatalogingException {

    	String fileType = fileName.substring(fileName.lastIndexOf('.'), fileName.length());            
        if (fileType.equalsIgnoreCase(".wsdl")) {

        	extrinsicObject.setObjectType(CanonicalConstants.CANONICAL_OBJECT_TYPE_ID_WSDL);           
            if (extrinsicObject instanceof ExtrinsicObjectType) {
                ((ExtrinsicObjectType)extrinsicObject).setMimeType("text/xml");
            }
        
        } else if (fileType.equalsIgnoreCase(".xsd")) {
            
        	extrinsicObject.setObjectType(CanonicalConstants.CANONICAL_OBJECT_TYPE_ID_XMLSchema);
            if (extrinsicObject instanceof ExtrinsicObjectType) {
                ((ExtrinsicObjectType)extrinsicObject).setMimeType("text/xml");
            }
        
        } else {
        	
            if (extrinsicObject instanceof ExtrinsicObjectType) {
                ((ExtrinsicObjectType)extrinsicObject).setMimeType("application/octet-stream");
            }

        }
        
    }

    private void resolveTypeImports(Element portTypeElement, RegistryObjectType extrinsicObject) throws CatalogingException { 

    	try {
    		
    		String id = getPortTypeId(portTypeElement);
    		if (idToRIMMap.get(id) == null) processPortType(portTypeElement);
    		
    		List<Element> typeElements = resolveTypes(wsdlDocument);    		
    		Iterator<Element> schemaItr = resolveSchemaExtensions(typeElements);
    		
    		while (schemaItr.hasNext()) {

    			Element schemaElement = schemaItr.next();
    			Iterator<Attr> schemaAttributeItr = wsdlDocument.getAttributes(schemaElement).iterator();
    			
    			String location = null;
    			String namespace = schemaElement.getNamespaceURI();

    			while (schemaAttributeItr.hasNext()) {
    				
    				Attr schemaAttribute = schemaAttributeItr.next();

    				String localAttrName = schemaAttribute.getLocalName();
    				if (localAttrName.equalsIgnoreCase("namespace")) {
    					namespace = schemaAttribute.getValue();

    				} else if (localAttrName.equalsIgnoreCase("schemaLocation")) {
    					location = schemaAttribute.getValue();
    				}
    				
    			}
    			
    			RegistryObjectType importedObject = catalogImportStatement(extrinsicObject, namespace, location);
    			if (importedObject != null) createAssociation_Imports(extrinsicObject, importedObject);

    		}

    	} catch (CatalogingException e) {
    		throw e;

    	} catch (Exception e) {
    		throw new CatalogingException("[WSDLCatalogerPlugin] " + e.getMessage());
    	
    	}

    }

    private void processBinding(Element bindingElement) throws CatalogingException {
            
    	try {

    		String id = getBindingId(bindingElement);               
            if (idToWSDLMap.containsKey(id)) return;
            
            InternationalStringType internationalString = null;

            // Create a RIM ExtrinsicObject instance for wsdl binding 
            ExtrinsicObjectType extrinsicObject = ebRIMFactory.createExtrinsicObjectType();
            
            idToRIMMap.put(id, extrinsicObject);
            idToWSDLMap.put(id, bindingElement);
            
            // Set id
            extrinsicObject.setId(id);
 
            // Set name
            LocalizedStringType nameType = ebRIMFactory.createLocalizedStringType();
            
            nameType.setLang(CanonicalConstants.DEFAULT_LANGUAGE);
            nameType.setValue(wsdlDocument.getAttribute(bindingElement, WSDLConstants.ATTR_NAME));
            
            internationalString = ebRIMFactory.createInternationalStringType();
            internationalString.getLocalizedString().add(nameType);
            
            extrinsicObject.setName(internationalString);
                
            // Set description
            Element docElement = wsdlDocument.getElement(bindingElement, WSDLConstants.QNAME_DOCUMENTATION);
            if (docElement != null) {
            
                LocalizedStringType descriptionType = ebRIMFactory.createLocalizedStringType();
                
                descriptionType.setLang(CanonicalConstants.DEFAULT_LANGUAGE);
                descriptionType.setValue(docElement.getFirstChild().getNodeValue());
                
                internationalString = ebRIMFactory.createInternationalStringType();
                internationalString.getLocalizedString().add(descriptionType);

                extrinsicObject.setDescription(internationalString);
            
            }
            
            catalogTargetNamespace(extrinsicObject);
            
            // Add wsdl Service Classification
            extrinsicObject.setObjectType(CanonicalConstants.CANONICAL_OBJECT_TYPE_ID_WSDL_BINDING);     
            createClassificationType(extrinsicObject, CanonicalConstants.CANONICAL_OBJECT_TYPE_ID_WSDL_BINDING);
            
            List<Element> soapBindingElements = wsdlDocument.getElements(bindingElement, WSDLConstants.QNAME_SOAP_BINDING);
            Iterator<Element> soapBindingElementItr = soapBindingElements.iterator();
                
            while (soapBindingElementItr.hasNext()) {
                
            	Element soapBindingElement = (Element)soapBindingElementItr.next();

                // Add SOAP Binding Classification
            	createClassificationType(extrinsicObject, CanonicalConstants.CANONICAL_PROTOCOL_TYPE_ID_SOAP);

                String transport = wsdlDocument.getAttribute(soapBindingElement, WSDLConstants.ATTR_TRANSPORT);
                if (transport.equals(WSDLConstants.URI_SOAP_TRANSPORT_HTTP)) {
                    createClassificationType(extrinsicObject, CanonicalConstants.CANONICAL_TRANSPORT_TYPE_ID_HTTP);
                }

                String soapStyleStr = wsdlDocument.getAttribute(soapBindingElement, WSDLConstants.ATTR_STYLE);
                String styleNode = null;
                
                if (soapStyleStr.equalsIgnoreCase(WSDLConstants.RPC)) {
                    styleNode = CanonicalConstants.CANONICAL_SOAP_STYLE_TYPE_ID_RPC;
                
                } else if (soapStyleStr.equalsIgnoreCase(WSDLConstants.DOCUMENT)) {
                    styleNode = CanonicalConstants.CANONICAL_SOAP_STYLE_TYPE_ID_DOCUMENT;
                
                }

                if (styleNode != null) {
                    // Add SOAP Style Classification
                    createClassificationType(extrinsicObject, styleNode);
                }
                
            }
            
            extrinsicObject.setMimeType("text/xml");
                
            // document get port type
            String portTypeStr = wsdlDocument.getAttribute(bindingElement, WSDLConstants.ATTR_TYPE);
            Element portTypeElement = resolvePortType(wsdlDocument, portTypeStr);

            if (portTypeElement == null) {
                throw new CatalogingException("[WSDLCatalogerPlugin] Missing required element");
                }
 
                processPortType(portTypeElement);

                // Set Implements Association between Binding and PortType
            ExtrinsicObjectType rimPortType = (ExtrinsicObjectType)idToRIMMap.get(getPortTypeId(portTypeElement));
            createAssociation_Implements(extrinsicObject, rimPortType);
        
    	} catch (CatalogingException ce) {
            throw ce;
        
    	} catch (Throwable e) {
            throw new CatalogingException("WSDLCatalogerPlugin] " + e.getMessage());
        }
    
    }

    private RegistryObjectType catalogImportStatement(RegistryObjectType registryObject, String importedNamespace, String location) throws CatalogingException {

    	// __DESIGN__
    	
    	// cases when xsd:include and xsd:redefine are used and there is no 
    	// namespace attribute are not taken into account. 
    	//
    	// Use the targetNamespace attribute of the <schema> element in the 
    	// <>.xsd file.

    	if ((importedNamespace == null) && (location == null)) {
    		// Ignore these cases: <xsd:import/>
    		return null;
    	}

    	RegistryObjectType importedRegistryObject = null;

    	try {

    		String entryId = null;
    		if (location != null) {
    			
    			entryId = location;
    			importedRegistryObject = getRegistryObject(entryId);
    		}
    		
    		if (importedRegistryObject == null) {

    			if (importedNamespace != null && entryId != null) {
    				
    				String absoluteFileName = FileUtil.getCompleteRelativeFileName(entryId);
    				String qualifiedId = getQualifiedId(importedNamespace, absoluteFileName);

    				importedRegistryObject = getRegistryObject(qualifiedId);

    			}

    		}

    		if (importedRegistryObject == null) {

    			if (location != null) {
    				importedRegistryObject = createExtrinsicObject(location);
    			}

    		}

    		if (importedRegistryObject == null) {          

    			// Check if namespace or schemaLocation is an absolute URL.
    			// If it is, create ExternalLink to the file
    			URL schemaLocationURL = null;                   

    			String id = null;
    			String namespaceURN = null;

    			if (location == null) {
    				
    				// Support case of just namespace attribute. e.g.
    				// <xsd:import namespace="http://www.w3.org/2001/xml.xsd"/  
    				URN urn = new URN(importedNamespace);
    				urn.makeValid();
    				
    				namespaceURN = urn.getURN();
    				location = importedNamespace;

    			} else {
    				
    				if (importedNamespace != null) {
    					
    					try {
    						URN urn = new URN(importedNamespace);
    						
    						urn.makeValid();
    						namespaceURN = urn.getURN();

    					} catch (URISyntaxException e) {
    						// Presume that the namespace is a URN
    						namespaceURN = importedNamespace;
    					
    					} 
    				
    				}

    				try {

    					schemaLocationURL = new URL(location);
    					id = XMLUtil.fixURN(namespaceURN + schemaLocationURL.getPath());

    				} catch (MalformedURLException e) {

    					if (registryObject instanceof ExtrinsicObjectType) {                            
    						new CatalogingException("[WSDLCatalogerPlugin] " + e.getMessage());
    					
    					} else {
    						// Create id to be used in lookup/creation of 
    						// an ExternalLinkType object below
    						id = XMLUtil.fixURN(namespaceURN + ":" + location);
    					
    					}

    				}

    			}

    			// Has this link been created previously during this request?
    			importedRegistryObject = (ExternalLinkType)idToRIMMap.get(id);
    			if (importedRegistryObject == null) {

    				// Does this link already exist in the Registry?
    				ReadManager rm = ReadManager.getInstance();
    				Object object = rm.getRegistryObject(id);
    				
    				if (object != null) importedRegistryObject = (ExternalLinkType)object;
 
    			}
    			
    			if (importedRegistryObject == null) {

    				if (XMLUtil.isValidURI(location)) {

    					// Create ExternalLink         
    					importedRegistryObject = ebRIMFactory.createExternalLinkType();
    					importedRegistryObject.setId(id);

    					SimpleLinkType externalRef = ebRIMFactory.createSimpleLinkType();
    					externalRef.setHref(location);
    					
    					((ExternalLinkType)importedRegistryObject).setExternalRef(externalRef);
    					idToRIMMap.put(location, importedRegistryObject);

    				}

    			}

    		} else {
    			updateRegistryObjectId(importedNamespace, importedRegistryObject, false);
    		
    		}

    	} catch (CatalogingException ce) {
    		throw ce;

    	} catch (Throwable t) {
    		throw new CatalogingException("[WSDLCatalogerPlugin] " + t.getMessage());
    	
    	}

    	return importedRegistryObject;
    
    }

    private void createClassificationType(RegistryObjectType registryObject, String classNode) {

        ClassificationType classificationType = RIMFactory.createClassification();
        
        classificationType.setClassifiedObject(registryObject.getId());
        classificationType.setClassificationNode(classNode);            

        registryObject.getClassification().add(classificationType);
        
    }
        
    private void processPortType(Element portTypeElement) throws CatalogingException {
    	
        try {

        	String id = getPortTypeId(portTypeElement);           
            if (idToWSDLMap.containsKey(id)) return;
            
            //Create a RIM ExtrinsicObject instance for wsdl portType 
            ExtrinsicObjectType extrinsicObject = ebRIMFactory.createExtrinsicObjectType();
            
            idToRIMMap.put(id, extrinsicObject);
            idToWSDLMap.put(id, portTypeElement);
            
            // Set id
            extrinsicObject.setId(id);
            
            InternationalStringType internationalString = null;
            
            // Set name
            LocalizedStringType nameType = ebRIMFactory.createLocalizedStringType();
            
            nameType.setLang(CanonicalConstants.DEFAULT_LANGUAGE);
            nameType.setValue(wsdlDocument.getAttribute(portTypeElement, WSDLConstants.ATTR_NAME));
            
            internationalString = ebRIMFactory.createInternationalStringType();
            internationalString.getLocalizedString().add(nameType);
            
            extrinsicObject.setName(internationalString);
            
            // Set description
            Element docElement = wsdlDocument.getElement(portTypeElement, WSDLConstants.QNAME_DOCUMENTATION);
            if (docElement != null) {
            
                LocalizedStringType descriptionType = ebRIMFactory.createLocalizedStringType();

                descriptionType.setLang(CanonicalConstants.DEFAULT_LANGUAGE);
                descriptionType.setValue(docElement.getFirstChild().getNodeValue());

                internationalString = ebRIMFactory.createInternationalStringType();
                internationalString.getLocalizedString().add(descriptionType);

            	extrinsicObject.setDescription(internationalString);
            
            }
            
            catalogTargetNamespace(extrinsicObject);
            
            // Add wsdl Service Classification
            extrinsicObject.setObjectType(CanonicalConstants.CANONICAL_OBJECT_TYPE_ID_WSDL_PORT_TYPE);  
            
            ClassificationType classificationType = RIMFactory.createClassification();
            
            classificationType.setClassifiedObject(extrinsicObject.getId());
            classificationType.setClassificationNode(CanonicalConstants.CANONICAL_OBJECT_TYPE_ID_WSDL_PORT_TYPE);
 
            extrinsicObject.getClassification().add(classificationType);
            extrinsicObject.setMimeType("text/xml"); 
            
        } catch (CatalogingException e) {
            throw e;
            
        } catch (Exception e) {
            throw new CatalogingException("WSDLCatalogerPlugin] " + e.getMessage());
        
        }   
        
    }
     
    private boolean isURNValid(String urnString) {
    	
        boolean isURNValid = true;
        try {
        
        	URN urn = new URN(urnString);
            urn.validate();
        
        } catch (URISyntaxException e) {
            isURNValid = false;
        }
        
        return isURNValid;
    
    }
    
    public String getQualifiedId(String namespaceURI, String id) throws CatalogingException {

    	if (id == null) {
            throw new CatalogingException("[WSDLCatalogerPlugin] Registry object with ID null.");
        }
        
        String qualifiedId = null;
        if (namespaceURI == null) {
            qualifiedId = id;
        } else {
            qualifiedId = namespaceURI + ":" + id;
        }

        //make sure id is a valid URN
        qualifiedId = makeValidURN(qualifiedId);
        
        return qualifiedId;
    }
     
    public String makeValidURN(String id) throws CatalogingException {

    	String qualifiedId = null;
        URN urn = new URN(id);
        
        try {
            urn.makeValid();                
            qualifiedId = urn.getURN();
        
        } catch (URISyntaxException ex) {
            throw new CatalogingException("[WSDLCatalogerPlugin] " + ex.getMessage());
        
        }
        
        return qualifiedId;
    
    }

    private void catalogImportStatements(RegistryObjectType registryObject, WSDLDocument wsdlDoc) throws CatalogingException {
        
    	Iterator<Element> itr = wsdlDoc.getElements(WSDLConstants.QNAME_IMPORT).iterator();
        while (itr.hasNext()) {

        	// Import maps to a wsdl:import element
            Element impt = (Element)itr.next();
            
            String namespace = impt.getAttribute(WSDLConstants.ATTR_NAMESPACE);
            String location = impt.getAttribute(WSDLConstants.ATTR_LOCATION);
            
            RegistryObjectType importedObject = catalogImportStatement(registryObject, namespace, location);        
            // Create Imports association between the current WSDLEO and 
            // the WSDL EO that it imports
            createAssociation_Imports(registryObject, importedObject);
        
        }
    }

    private Iterator<Element> resolveSchemaExtensions(List<Element> typeElements) throws CatalogingException {

    	List<Element> schemaElementChildrenList = new ArrayList<Element>();
        Iterator<Element> typeElementItr = typeElements.iterator();
        
        while (typeElementItr.hasNext()) {
            
        	Element typeElement = (Element)typeElementItr.next();
            List<Element> schemaElements = wsdlDocument.getElements(typeElement, WSDLConstants.QNAME_XSD_SCHEMA);
            
            Iterator<Element> schemaElementsItr = schemaElements.iterator();
            while (schemaElementsItr.hasNext()) {
                
            	Element schemaElement = (Element)schemaElementsItr.next();
                // Check for xsd:import
                List<Element> importElements = wsdlDocument.getElements(schemaElement, WSDLConstants.QNAME_XSD_IMPORT);
                schemaElementChildrenList.addAll(importElements);
                
                // Check for xsd:redefine
                List<Element> redefineElements = wsdlDocument.getElements(schemaElement, WSDLConstants.QNAME_XSD_REDEFINE);
                schemaElementChildrenList.addAll(redefineElements);
                
                // Check for xsd:include
                List<Element> includeElements = wsdlDocument.getElements(schemaElement, WSDLConstants.QNAME_XSD_INCLUDE);
                schemaElementChildrenList.addAll(includeElements);
            }
        }
        
        return schemaElementChildrenList.iterator();

    }

    private Element resolveBinding(WSDLDocument wsdlDocument, String portBindingStr) throws CatalogingException {
            
    	Element resolvedBinding = null;
        List<Element> bindings = wsdlDocument.getElements(WSDLConstants.QNAME_BINDING);
            
        Iterator<Element> bindingItr = bindings.iterator();        
        QName portBindingQName = createQName(null, portBindingStr);
        
        while (bindingItr.hasNext()) {
            
        	Element binding = (Element)bindingItr.next();
            String bindingTypeStr = wsdlDocument.getRequiredAttribute(binding, WSDLConstants.ATTR_NAME);
            
            // Get target namespace
            String bindingTargetNS = wsdlDocument.getTargetNamespaceURI(binding, WSDLConstants.QNAME_BINDING);
            // Try to resolve based on namespace:localValue
            QName bindingTypeQName = createQName(bindingTargetNS, bindingTypeStr);
            
            if (portBindingQName.equals(bindingTypeQName) ||
            
            		bindingTypeStr.equals(portBindingStr)) {
                	resolvedBinding = binding;
                
                	break;
            
            }
        }
        
        return resolvedBinding;
    }

    private Element resolvePortType(WSDLDocument wsdlCatalogedDocument, String bindingTypeStr) throws CatalogingException {
            
    	Element resolvedPortType = null;
        List<Element> portTypes = wsdlDocument.getElements(WSDLConstants.QNAME_PORT_TYPE);
            
        Iterator<Element> portTypeItr = portTypes.iterator();        
            
        QName portBindingQName = createQName(null, bindingTypeStr);
        while (portTypeItr.hasNext()) {

        	Element portType = (Element)portTypeItr.next();
            String portTypeStr = wsdlDocument.getRequiredAttribute(portType, WSDLConstants.ATTR_NAME);
            
            String portTypeTargetNS = wsdlDocument.getTargetNamespaceURI(portType, WSDLConstants.QNAME_PORT_TYPE);
            // Try to resolve based on namespace:localValue
            QName bindingTypeQName = createQName(portTypeTargetNS, portTypeStr);
            if (portBindingQName.equals(bindingTypeQName) ||
            
            		portTypeStr.equals(bindingTypeStr)) {
                	resolvedPortType = portType;
                	break;
            }
        }
        
        return resolvedPortType;
        
    }

    private QName createQName(String namespace, String attrValue) {
        
    	QName qname = null;
        if (namespace == null && attrValue == null) {
            qname = null;
        
        } else {
            
        	if (namespace == null) {
                
        		String prefix = XMLUtil.getPrefix(attrValue);
                if (prefix == null) {
                    qname = new QName("", attrValue);
                
                } else {
                    namespace = wsdlDocument.getNamespaceURI(prefix);
                    qname = new QName(namespace, XMLUtil.getLocalPart(attrValue));
                }
            
        	} else {
                qname = new QName(namespace, attrValue);
            
        	}
        
        }
        
        return qname;
    
    }

}
