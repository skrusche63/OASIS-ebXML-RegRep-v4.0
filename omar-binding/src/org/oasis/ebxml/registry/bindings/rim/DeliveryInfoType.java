//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.02.03 at 05:40:14 PM MEZ 
//


package org.oasis.ebxml.registry.bindings.rim;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.w3._2005._08.addressing.EndpointReferenceType;


/**
 * 
 *         Contains the information needed by the server to deliver notifications for the subscription. 
 *         It includes the reference to the endpoint where notifications should be delivered.
 *       
 * 
 * <p>Java class for DeliveryInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DeliveryInfoType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:4.0}ExtensibleObjectType">
 *       &lt;sequence>
 *         &lt;element name="NotifyTo" type="{http://www.w3.org/2005/08/addressing}EndpointReferenceType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="notificationOption" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:4.0}objectReferenceType" default="urn:oasis:names:tc:ebxml-regrep:NotificationOptionType:ObjectRefs" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeliveryInfoType", propOrder = {
    "notifyTo"
})
public class DeliveryInfoType
    extends ExtensibleObjectType
{

    @XmlElement(name = "NotifyTo", required = true)
    protected EndpointReferenceType notifyTo;
    @XmlAttribute
    protected String notificationOption;

    /**
     * Gets the value of the notifyTo property.
     * 
     * @return
     *     possible object is
     *     {@link EndpointReferenceType }
     *     
     */
    public EndpointReferenceType getNotifyTo() {
        return notifyTo;
    }

    /**
     * Sets the value of the notifyTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link EndpointReferenceType }
     *     
     */
    public void setNotifyTo(EndpointReferenceType value) {
        this.notifyTo = value;
    }

    /**
     * Gets the value of the notificationOption property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotificationOption() {
        if (notificationOption == null) {
            return "urn:oasis:names:tc:ebxml-regrep:NotificationOptionType:ObjectRefs";
        } else {
            return notificationOption;
        }
    }

    /**
     * Sets the value of the notificationOption property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificationOption(String value) {
        this.notificationOption = value;
    }

}
