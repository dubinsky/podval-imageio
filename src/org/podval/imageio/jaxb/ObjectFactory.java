//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vBeta 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2003.02.10 at 03:11:58 EST 
//


package org.podval.imageio.jaxb;

public class ObjectFactory
    extends com.sun.xml.bind.DefaultJAXBContextImpl
{


    static {
        com.sun.xml.bind.ImplementationRegistry ir = com.sun.xml.bind.ImplementationRegistry.getInstance();
        ir.setDefaultImpl((org.podval.imageio.jaxb.MakerNoteMarker.class), (org.podval.imageio.jaxb.impl.MakerNoteMarkerImpl.class));
        ir.setDefaultImpl((org.podval.imageio.jaxb.Record.class), (org.podval.imageio.jaxb.impl.RecordImpl.class));
        ir.setDefaultImpl((org.podval.imageio.jaxb.MakerNote.class), (org.podval.imageio.jaxb.impl.MakerNoteImpl.class));
        ir.setDefaultImpl((org.podval.imageio.jaxb.MetaMetadataType.class), (org.podval.imageio.jaxb.impl.MetaMetadataTypeImpl.class));
        ir.setDefaultImpl((org.podval.imageio.jaxb.MetaMetadataType.Record.class), (org.podval.imageio.jaxb.impl.MetaMetadataTypeImpl.RecordImpl.class));
        ir.setDefaultImpl((org.podval.imageio.jaxb.MetaMetadata.class), (org.podval.imageio.jaxb.impl.MetaMetadataImpl.class));
        ir.setDefaultImpl((org.podval.imageio.jaxb.Field.class), (org.podval.imageio.jaxb.impl.FieldImpl.class));
        ir.setDefaultImpl((org.podval.imageio.jaxb.SubDirectory.class), (org.podval.imageio.jaxb.impl.SubDirectoryImpl.class));
        ir.setDefaultImpl((org.podval.imageio.jaxb.Directory.class), (org.podval.imageio.jaxb.impl.DirectoryImpl.class));
        ir.setDefaultImpl((org.podval.imageio.jaxb.Directory.MakerNoteMarker.class), (org.podval.imageio.jaxb.impl.DirectoryImpl.MakerNoteMarkerImpl.class));
        ir.setDefaultImpl((org.podval.imageio.jaxb.Enumeration.class), (org.podval.imageio.jaxb.impl.EnumerationImpl.class));
        ir.setDefaultImpl((org.podval.imageio.jaxb.EnumItem.class), (org.podval.imageio.jaxb.impl.EnumItemImpl.class));
        ir.setDefaultImpl((org.podval.imageio.jaxb.SubRecord.class), (org.podval.imageio.jaxb.impl.SubRecordImpl.class));
        ir.setDefaultImpl((org.podval.imageio.jaxb.MetaMetadataType.Directory.class), (org.podval.imageio.jaxb.impl.MetaMetadataTypeImpl.DirectoryImpl.class));
        ir.setDefaultImpl((org.podval.imageio.jaxb.MetaMetadataType.MakerNote.class), (org.podval.imageio.jaxb.impl.MetaMetadataTypeImpl.MakerNoteImpl.class));
        ir.setDefaultImpl((org.podval.imageio.jaxb.Enumeration.Item.class), (org.podval.imageio.jaxb.impl.EnumerationImpl.ItemImpl.class));
        ir.setDefaultImpl((org.podval.imageio.jaxb.Subdirectory.class), (org.podval.imageio.jaxb.impl.SubdirectoryImpl.class));
        ir.setDefaultImpl((org.podval.imageio.jaxb.Typed.class), (org.podval.imageio.jaxb.impl.TypedImpl.class));
        ir.setDefaultImpl((org.podval.imageio.jaxb.Directory.Record.class), (org.podval.imageio.jaxb.impl.DirectoryImpl.RecordImpl.class));
    }

    public ObjectFactory() {
        super(new org.podval.imageio.jaxb.ObjectFactory.GrammarInfoImpl());
    }

    /**
     * Create an instance of the specified Java content interface.
     * If the Java content interface has been replaced via the 
     * setImplementation method, than an instance of the client 
     * specified implementation class will be instantiated instead.
     * 
     * @param javaContentInterface the Class object of the javacontent interface to instantiate
     * @return a new instance
     * @throws JAXBException if an error occurs
     */
    public static Object newInstance(Class javaContentInterface)
        throws javax.xml.bind.JAXBException
    {
        return com.sun.xml.bind.DefaultJAXBContextImpl.newInstance(javaContentInterface);
    }

    public static org.podval.imageio.jaxb.MakerNoteMarker createMakerNoteMarker()
        throws javax.xml.bind.JAXBException
    {
        return ((org.podval.imageio.jaxb.MakerNoteMarker) newInstance((org.podval.imageio.jaxb.MakerNoteMarker.class)));
    }

    public static org.podval.imageio.jaxb.Record createRecord()
        throws javax.xml.bind.JAXBException
    {
        return ((org.podval.imageio.jaxb.Record) newInstance((org.podval.imageio.jaxb.Record.class)));
    }

    public static org.podval.imageio.jaxb.MakerNote createMakerNote()
        throws javax.xml.bind.JAXBException
    {
        return ((org.podval.imageio.jaxb.MakerNote) newInstance((org.podval.imageio.jaxb.MakerNote.class)));
    }

    public static org.podval.imageio.jaxb.MetaMetadataType createMetaMetadataType()
        throws javax.xml.bind.JAXBException
    {
        return ((org.podval.imageio.jaxb.MetaMetadataType) newInstance((org.podval.imageio.jaxb.MetaMetadataType.class)));
    }

    public static org.podval.imageio.jaxb.MetaMetadataType.Record createMetaMetadataTypeRecord()
        throws javax.xml.bind.JAXBException
    {
        return ((org.podval.imageio.jaxb.MetaMetadataType.Record) newInstance((org.podval.imageio.jaxb.MetaMetadataType.Record.class)));
    }

    public static org.podval.imageio.jaxb.MetaMetadata createMetaMetadata()
        throws javax.xml.bind.JAXBException
    {
        return ((org.podval.imageio.jaxb.MetaMetadata) newInstance((org.podval.imageio.jaxb.MetaMetadata.class)));
    }

    public static org.podval.imageio.jaxb.Field createField()
        throws javax.xml.bind.JAXBException
    {
        return ((org.podval.imageio.jaxb.Field) newInstance((org.podval.imageio.jaxb.Field.class)));
    }

    public static org.podval.imageio.jaxb.SubDirectory createSubDirectory()
        throws javax.xml.bind.JAXBException
    {
        return ((org.podval.imageio.jaxb.SubDirectory) newInstance((org.podval.imageio.jaxb.SubDirectory.class)));
    }

    public static org.podval.imageio.jaxb.Directory createDirectory()
        throws javax.xml.bind.JAXBException
    {
        return ((org.podval.imageio.jaxb.Directory) newInstance((org.podval.imageio.jaxb.Directory.class)));
    }

    public static org.podval.imageio.jaxb.Directory.MakerNoteMarker createDirectoryMakerNoteMarker()
        throws javax.xml.bind.JAXBException
    {
        return ((org.podval.imageio.jaxb.Directory.MakerNoteMarker) newInstance((org.podval.imageio.jaxb.Directory.MakerNoteMarker.class)));
    }

    public static org.podval.imageio.jaxb.Enumeration createEnumeration()
        throws javax.xml.bind.JAXBException
    {
        return ((org.podval.imageio.jaxb.Enumeration) newInstance((org.podval.imageio.jaxb.Enumeration.class)));
    }

    public static org.podval.imageio.jaxb.EnumItem createEnumItem()
        throws javax.xml.bind.JAXBException
    {
        return ((org.podval.imageio.jaxb.EnumItem) newInstance((org.podval.imageio.jaxb.EnumItem.class)));
    }

    public static org.podval.imageio.jaxb.SubRecord createSubRecord()
        throws javax.xml.bind.JAXBException
    {
        return ((org.podval.imageio.jaxb.SubRecord) newInstance((org.podval.imageio.jaxb.SubRecord.class)));
    }

    public static org.podval.imageio.jaxb.MetaMetadataType.Directory createMetaMetadataTypeDirectory()
        throws javax.xml.bind.JAXBException
    {
        return ((org.podval.imageio.jaxb.MetaMetadataType.Directory) newInstance((org.podval.imageio.jaxb.MetaMetadataType.Directory.class)));
    }

    public static org.podval.imageio.jaxb.MetaMetadataType.MakerNote createMetaMetadataTypeMakerNote()
        throws javax.xml.bind.JAXBException
    {
        return ((org.podval.imageio.jaxb.MetaMetadataType.MakerNote) newInstance((org.podval.imageio.jaxb.MetaMetadataType.MakerNote.class)));
    }

    public static org.podval.imageio.jaxb.Enumeration.Item createEnumerationItem()
        throws javax.xml.bind.JAXBException
    {
        return ((org.podval.imageio.jaxb.Enumeration.Item) newInstance((org.podval.imageio.jaxb.Enumeration.Item.class)));
    }

    public static org.podval.imageio.jaxb.Subdirectory createSubdirectory()
        throws javax.xml.bind.JAXBException
    {
        return ((org.podval.imageio.jaxb.Subdirectory) newInstance((org.podval.imageio.jaxb.Subdirectory.class)));
    }

    public static org.podval.imageio.jaxb.Typed createTyped()
        throws javax.xml.bind.JAXBException
    {
        return ((org.podval.imageio.jaxb.Typed) newInstance((org.podval.imageio.jaxb.Typed.class)));
    }

    public static org.podval.imageio.jaxb.Directory.Record createDirectoryRecord()
        throws javax.xml.bind.JAXBException
    {
        return ((org.podval.imageio.jaxb.Directory.Record) newInstance((org.podval.imageio.jaxb.Directory.Record.class)));
    }

    private static class GrammarInfoImpl
        extends com.sun.xml.bind.GrammarInfo
    {


        public Class getRootElement(String uri, String local) {
            if ("".equals(uri)&&"meta-metadata".equals(local)) {
                return (org.podval.imageio.jaxb.impl.MetaMetadataImpl.class);
            }
            return null;
        }

        public String[] getProbePoints() {
            return new java.lang.String[] {"", "meta-metadata"};
        }

    }

}
