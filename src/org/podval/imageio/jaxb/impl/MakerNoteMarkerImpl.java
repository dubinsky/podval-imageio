//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vBeta 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2003.02.10 at 03:11:58 EST 
//


package org.podval.imageio.jaxb.impl;

public class MakerNoteMarkerImpl implements org.podval.imageio.jaxb.MakerNoteMarker, com.sun.xml.bind.unmarshaller.UnmarshallableObject, com.sun.xml.bind.serializer.XMLSerializable, com.sun.xml.bind.validator.ValidatableObject
{

    private final static Class PRIMARY_INTERFACE_CLASS = org.podval.imageio.jaxb.MakerNoteMarker.class;
    protected String _Type;
    protected boolean has_Tag;
    protected int _Tag;
    private final static com.sun.msv.grammar.Grammar schemaFragment = com.sun.xml.bind.validator.SchemaDeserializer.deserialize("\u00ac\u00ed\u0000\u0005sr\u0000\u001fcom.sun.msv.grammar.SequenceExp\u0091Ue\u00f1\u0017p\u00f3\u00ad\u0002\u0000\u0000xr\u0000\u001dcom.sun.msv.grammar.BinaryExp\u009f\u008d\u000fi<\u00c1_\u00b7\u0002\u0000\u0002L\u0000\u0004exp1t\u0000 Lcom/sun/msv/grammar/Expression;L\u0000\u0004exp2q\u0000~\u0000\u0002xr\u0000\u001ecom.sun.msv.grammar.Expression\u00f8\u0018\u0082\u00e8N5~O\u0002\u0000\u0003I\u0000\u000ecachedHashCodeL\u0000\u0013epsilonReducibilityt\u0000\u0013Ljava/lang/Boolean;L\u0000\u000bexpandedExpq\u0000~\u0000\u0002xp\u0003\u00ea\u00a4\u00c7ppsr\u0000 com.sun.msv.grammar.AttributeExp0\rR\n\u00c7L\n\u0099\u0002\u0000\u0002L\u0000\u0003expq\u0000~\u0000\u0002L\u0000\tnameClasst\u0000\u001fLcom/sun/msv/grammar/NameClass;xq\u0000~\u0000\u0003\u0002+LZppsr\u0000\u001bcom.sun.msv.grammar.DataExp8\u00f5\u00f5>{j!\u00cb\u0002\u0000\u0003L\u0000\u0002dtt\u0000\u001fLorg/relaxng/datatype/Datatype;L\u0000\u0006exceptq\u0000~\u0000\u0002L\u0000\u0004namet\u0000\u001dLcom/sun/msv/util/StringPair;xq\u0000~\u0000\u0003\u0000?\u00bc\fppsr\u0000)com.sun.msv.datatype.xsd.EnumerationFacetp\u008f\u0089\u001d\u0098\u00c5.\u007f\u0002\u0000\u0001L\u0000\u0006valuest\u0000\u000fLjava/util/Set;xr\u00009com.sun.msv.datatype.xsd.DataTypeWithValueConstraintFacet\"\u00a7Ro\u00ca\u00c7\u008aT\u0002\u0000\u0000xr\u0000*com.sun.msv.datatype.xsd.DataTypeWithFacet\u00f4\u00cf\u00e6D\u00b4\u00a8k\u0000\u0002\u0000\u0005Z\u0000\fisFacetFixedZ\u0000\u0012needValueCheckFlagL\u0000\bbaseTypet\u0000)Lcom/sun/msv/datatype/xsd/XSDatatypeImpl;L\u0000\fconcreteTypet\u0000\'Lcom/sun/msv/datatype/xsd/ConcreteType;L\u0000\tfacetNamet\u0000\u0012Ljava/lang/String;xr\u0000\'com.sun.msv.datatype.xsd.XSDatatypeImpl4\u00e9\u0099H.\u00872z\u0002\u0000\u0003L\u0000\fnamespaceUriq\u0000~\u0000\u0013L\u0000\btypeNameq\u0000~\u0000\u0013L\u0000\nwhiteSpacet\u0000.Lcom/sun/msv/datatype/xsd/WhiteSpaceProcessor;xpt\u0000\u0000t\u0000\u0004Typesr\u0000.com.sun.msv.datatype.xsd.WhiteSpaceProcessor$1\u0013JMoI\u00db\u00a4G\u0002\u0000\u0000xr\u0000,com.sun.msv.datatype.xsd.WhiteSpaceProcessorip\u00ff0C\u00ce\u000eN\u0002\u0000\u0000xp\u0000\u0000sr\u0000#com.sun.msv.datatype.xsd.StringType\u00c0\t\u00a9y\u00f6\u0011\u009b\u00e6\u0002\u0000\u0000xr\u0000*com.sun.msv.datatype.xsd.BuiltinAtomicType\u0002\u00ff10\u00a8bR\u00ca\u0002\u0000\u0000xr\u0000%com.sun.msv.datatype.xsd.ConcreteType7\u00adsa|\u00d7Z\u001d\u0002\u0000\u0000xq\u0000~\u0000\u0014t\u0000 http://www.w3.org/2001/XMLSchemat\u0000\u0006stringq\u0000~\u0000\u001bq\u0000~\u0000\u001ft\u0000\u000benumerationsr\u0000\u0011java.util.HashSet\u00baD\u0085\u0095\u0096\u00b8\u00b74\u0003\u0000\u0000xpw\f\u0000\u0000\u0000 ?@\u0000\u0000\u0000\u0000\u0000\u0011t\u0000\u0003onet\u0000\u0003U16t\u0000\brationalt\u0000\u0003S16t\u0000\u0003X32t\u0000\u0002X8t\u0000\u0003X16t\u0000\tstructuret\u0000\u0003F32t\u0000\u0003S32t\u0000\u0006stringt\u0000\u0003twot\u0000\u0003U32t\u0000\u0002U8t\u0000\bU16orU32t\u0000\tX8-stringt\u0000\u000esignedRationalxsr\u00000com.sun.msv.grammar.Expression$NullSetExpression s\u0080\u0089\u0096\u00cf\u009a@\u0002\u0000\u0000xq\u0000~\u0000\u0003\u0000\u0000\u0000\nsr\u0000\u0011java.lang.Boolean\u00cd r\u0080\u00d5\u009c\u00fa\u00ee\u0002\u0000\u0001Z\u0000\u0005valuexp\u0000psr\u0000\u001bcom.sun.msv.util.StringPair\u00d0t\u001ejB\u008f\u008d\u00a0\u0002\u0000\u0002L\u0000\tlocalNameq\u0000~\u0000\u0013L\u0000\fnamespaceURIq\u0000~\u0000\u0013xpt\u0000\u000estring-derivedq\u0000~\u0000\u0017sr\u0000#com.sun.msv.grammar.SimpleNameClass\u0091\u00e9\u00f7\u008d\u00ab\u0010\u00aa\u00f5\u0002\u0000\u0002L\u0000\tlocalNameq\u0000~\u0000\u0013L\u0000\fnamespaceURIq\u0000~\u0000\u0013xr\u0000\u001dcom.sun.msv.grammar.NameClass\u009c}\u00d4kB\u00c7\tk\u0002\u0000\u0000xpt\u0000\u0004typeq\u0000~\u0000\u0017sq\u0000~\u0000\u0006\u0001\u00bfXhppsq\u0000~\u0000\t\u0001pm\u00b8q\u0000~\u00009psr\u0000*com.sun.msv.datatype.xsd.UnsignedShortTypeu\u00b2\u009dc\u00b9\u00c9\f6\u0002\u0000\u0000xr\u0000 com.sun.msv.datatype.xsd.IntType\u00bel\u00b6\u0018\u00b0\u0014\u00e4\u0014\u0002\u0000\u0000xr\u0000+com.sun.msv.datatype.xsd.IntegerDerivedType\u0099\u00f1]\u0090&6k\u00be\u0002\u0000\u0000xq\u0000~\u0000\u001dq\u0000~\u0000 t\u0000\runsignedShortsr\u0000.com.sun.msv.datatype.xsd.WhiteSpaceProcessor$2\u0087z9\u00ee\u00f8,N\u0005\u0002\u0000\u0000xq\u0000~\u0000\u001aq\u0000~\u00007sq\u0000~\u0000:q\u0000~\u0000Gq\u0000~\u0000 sq\u0000~\u0000=t\u0000\u0003tagq\u0000~\u0000\u0017sr\u0000\"com.sun.msv.grammar.ExpressionPool\u00e5\u00f3J;\u00cd]^\u00f8\u0002\u0000\u0001L\u0000\bexpTablet\u0000/Lcom/sun/msv/grammar/ExpressionPool$ClosedHash;xpsr\u0000-com.sun.msv.grammar.ExpressionPool$ClosedHash\u00d7j\u00d0N\u00ef\u00e8\u00ed\u001c\u0002\u0000\u0004I\u0000\u0005countI\u0000\tthresholdL\u0000\u0006parentq\u0000~\u0000N[\u0000\u0005tablet\u0000![Lcom/sun/msv/grammar/Expression;xp\u0000\u0000\u0000\u0001\u0000\u0000\u00009pur\u0000![Lcom.sun.msv.grammar.Expression;\u00d68D\u00c3]\u00ad\u00a7\n\u0002\u0000\u0000xp\u0000\u0000\u0000\u00bfppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppq\u0000~\u0000\u0005pppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp");

    public String getType() {
        return _Type;
    }

    public void setType(String value) {
        _Type = value;
    }

    public int getTag() {
        return _Tag;
    }

    public void setTag(int value) {
        _Tag = value;
        has_Tag = true;
    }

    public com.sun.xml.bind.unmarshaller.ContentHandlerEx getUnmarshaller(com.sun.xml.bind.unmarshaller.UnmarshallingContext context) {
        return new org.podval.imageio.jaxb.impl.MakerNoteMarkerImpl.Unmarshaller(context);
    }

    public Class getPrimaryInterfaceClass() {
        return PRIMARY_INTERFACE_CLASS;
    }

    public void serializeElements(com.sun.xml.bind.serializer.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
    }

    public void serializeAttributes(com.sun.xml.bind.serializer.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
        context.startAttribute("", "type");
        context.text(((String) _Type));
        context.endAttribute();
        context.startAttribute("", "tag");
        context.text(com.sun.msv.datatype.xsd.IntType.save(((Integer) new java.lang.Integer(_Tag))));
        context.endAttribute();
    }

    public void serializeAttributeBodies(com.sun.xml.bind.serializer.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
    }

    public Class getPrimaryInterface() {
        return (org.podval.imageio.jaxb.MakerNoteMarker.class);
    }

    public com.sun.msv.verifier.DocumentDeclaration createRawValidator() {
        return new com.sun.msv.verifier.regexp.REDocumentDeclaration(schemaFragment);
    }

    public class Unmarshaller
        extends com.sun.xml.bind.unmarshaller.ContentHandlerEx
    {


        public Unmarshaller(com.sun.xml.bind.unmarshaller.UnmarshallingContext context) {
            super(context, "-----");
        }

        protected com.sun.xml.bind.unmarshaller.UnmarshallableObject owner() {
            return org.podval.imageio.jaxb.impl.MakerNoteMarkerImpl.this;
        }

        public void enterElement(String ___uri, String ___local, org.xml.sax.Attributes __atts)
            throws com.sun.xml.bind.unmarshaller.UnreportedException
        {
            switch (state) {
                case  0 :
                    revertToParentFromEnterElement(___uri, ___local, __atts);
                    return ;
            }
            super.enterElement(___uri, ___local, __atts);
        }

        public void leaveElement(String ___uri, String ___local)
            throws com.sun.xml.bind.unmarshaller.UnreportedException
        {
            switch (state) {
                case  0 :
                    revertToParentFromLeaveElement(___uri, ___local);
                    return ;
            }
            super.leaveElement(___uri, ___local);
        }

        public void enterAttribute(String ___uri, String ___local)
            throws com.sun.xml.bind.unmarshaller.UnreportedException
        {
            switch (state) {
                case  0 :
                    if ("".equals(___uri)&&"type".equals(___local)) {
                        state = 1;
                        return ;
                    }
                    if ("".equals(___uri)&&"tag".equals(___local)) {
                        state = 3;
                        return ;
                    }
                    revertToParentFromEnterAttribute(___uri, ___local);
                    return ;
            }
            super.enterAttribute(___uri, ___local);
        }

        public void leaveAttribute(String ___uri, String ___local)
            throws com.sun.xml.bind.unmarshaller.UnreportedException
        {
            switch (state) {
                case  0 :
                    revertToParentFromLeaveAttribute(___uri, ___local);
                    return ;
                case  2 :
                    if ("".equals(___uri)&&"type".equals(___local)) {
                        goto0();
                        return ;
                    }
                    break;
                case  4 :
                    if ("".equals(___uri)&&"tag".equals(___local)) {
                        goto0();
                        return ;
                    }
                    break;
            }
            super.leaveAttribute(___uri, ___local);
        }

        public void text(String value)
            throws com.sun.xml.bind.unmarshaller.UnreportedException
        {
            try {
                switch (state) {
                    case  0 :
                        revertToParentFromText(value);
                        return ;
                    case  3 :
                        _Tag = com.sun.msv.datatype.xsd.IntType.load(com.sun.xml.bind.WhiteSpaceProcessor.collapse(value)).intValue();
                        has_Tag = true;
                        state = 4;
                        return ;
                    case  1 :
                        _Type = value;
                        state = 2;
                        return ;
                }
            } catch (RuntimeException e) {
                handleUnexpectedTextException(value, e);
            }
        }

        private void goto0()
            throws com.sun.xml.bind.unmarshaller.UnreportedException
        {
            int idx;
            state = 0;
            idx = context.getAttribute("", "type");
            if (idx >= 0) {
                context.consumeAttribute(idx);
                return ;
            }
            idx = context.getAttribute("", "tag");
            if (idx >= 0) {
                context.consumeAttribute(idx);
                return ;
            }
        }

    }

}