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
 * &lt;complexType name="MakerNote">
 *   &lt;complexContent>
 *     &lt;extension base="{}Directory">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="record" type="{}SubRecord"/>
 *         &lt;element name="directory" type="{}SubDirectory"/>
 *         &lt;element name="makerNoteMarker" type="{}MakerNoteMarker"/>
 *       &lt;/choice>
 *       &lt;attribute name="make" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="signature" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public interface MakerNote
    extends org.podval.imageio.jaxb.Directory
{


    String getSignature();

    void setSignature(String value);

    String getMake();

    void setMake(String value);

}
