//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.02.03 at 05:40:14 PM MEZ 
//


package org.oasis.ebxml.registry.bindings.rim;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *         Represents a service in ebRIM.
 *         Matches service as defined in WSDL 2.
 *       
 * 
 * <p>Java class for ServiceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:4.0}RegistryObjectType">
 *       &lt;sequence>
 *         &lt;element name="ServiceEndpoint" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:4.0}ServiceEndpointType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="serviceInterface" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:4.0}objectReferenceType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceType", propOrder = {
    "serviceEndpoint"
})
public class ServiceType
    extends RegistryObjectType
{

    @XmlElement(name = "ServiceEndpoint")
    protected List<ServiceEndpointType> serviceEndpoint;
    @XmlAttribute
    protected String serviceInterface;

    /**
     * Gets the value of the serviceEndpoint property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the serviceEndpoint property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getServiceEndpoint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ServiceEndpointType }
     * 
     * 
     */
    public List<ServiceEndpointType> getServiceEndpoint() {
        if (serviceEndpoint == null) {
            serviceEndpoint = new ArrayList<ServiceEndpointType>();
        }
        return this.serviceEndpoint;
    }

    /**
     * Gets the value of the serviceInterface property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceInterface() {
        return serviceInterface;
    }

    /**
     * Sets the value of the serviceInterface property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceInterface(String value) {
        this.serviceInterface = value;
    }

}
