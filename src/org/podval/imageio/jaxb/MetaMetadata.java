//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vBeta 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2003.02.10 at 03:11:58 EST 
//


package org.podval.imageio.jaxb;


/**
 * The following schema fragment specifies the expected content contained within this java content object.
 * <p>
 * <pre>
 * &lt;element name="meta-metadata">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="record" type="{}Record"/>
 *           &lt;element name="directory" type="{}Directory"/>
 *           &lt;element name="makerNote" type="{}MakerNote"/>
 *         &lt;/choice>
 *       &lt;/restriction>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 * 
 */
public interface MetaMetadata
    extends javax.xml.bind.Element, org.podval.imageio.jaxb.MetaMetadataType
{


}
