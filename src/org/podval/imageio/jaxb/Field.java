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
 * &lt;complexType name="Field">
 *   &lt;complexContent>
 *     &lt;extension base="{}Typed">
 *       &lt;choice>
 *         &lt;element name="enumeration" type="{}Enumeration" minOccurs="0"/>
 *         &lt;element name="field" type="{}Field" maxOccurs="unbounded" minOccurs="2"/>
 *       &lt;/choice>
 *       &lt;attribute name="index" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public interface Field
    extends org.podval.imageio.jaxb.Typed
{


    int getIndex();

    void setIndex(int value);

    boolean isSetIndex();

    void unsetIndex();

    org.podval.imageio.jaxb.Enumeration getEnumeration();

    void setEnumeration(org.podval.imageio.jaxb.Enumeration value);

    java.util.List getSubfields();

}
