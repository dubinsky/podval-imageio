//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vBeta 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2003.02.10 at 03:11:58 EST 
//


package org.podval.imageio.jaxb.impl;

public class RecordImpl
    extends org.podval.imageio.jaxb.impl.TypedImpl
    implements org.podval.imageio.jaxb.Record, com.sun.xml.bind.unmarshaller.UnmarshallableObject, com.sun.xml.bind.serializer.XMLSerializable, com.sun.xml.bind.validator.ValidatableObject
{

    private final static Class PRIMARY_INTERFACE_CLASS = org.podval.imageio.jaxb.Record.class;
    protected boolean has_Vector;
    protected boolean _Vector;
    protected org.podval.imageio.jaxb.Enumeration _Enumeration;
    protected java.util.ArrayList _Fields = new java.util.ArrayList();
    protected Comparable _Count;
    private final static org.relaxng.datatype.Datatype ___dt0 = com.sun.xml.bind.unmarshaller.DatatypeDeserializer.deserialize("\u00ac\u00ed\u0000\u0005sr\u0000)com.sun.msv.datatype.xsd.EnumerationFacetp\u008f\u0089\u001d\u0098\u00c5.\u007f\u0002\u0000\u0001L\u0000\u0006valuest\u0000\u000fLjava/util/Set;xr\u00009com.sun.msv.datatype.xsd.DataTypeWithValueConstraintFacet\"\u00a7Ro\u00ca\u00c7\u008aT\u0002\u0000\u0000xr\u0000*com.sun.msv.datatype.xsd.DataTypeWithFacet\u00f4\u00cf\u00e6D\u00b4\u00a8k\u0000\u0002\u0000\u0005Z\u0000\fisFacetFixedZ\u0000\u0012needValueCheckFlagL\u0000\bbaseTypet\u0000)Lcom/sun/msv/datatype/xsd/XSDatatypeImpl;L\u0000\fconcreteTypet\u0000\'Lcom/sun/msv/datatype/xsd/ConcreteType;L\u0000\tfacetNamet\u0000\u0012Ljava/lang/String;xr\u0000\'com.sun.msv.datatype.xsd.XSDatatypeImpl4\u00e9\u0099H.\u00872z\u0002\u0000\u0003L\u0000\fnamespaceUriq\u0000~\u0000\u0006L\u0000\btypeNameq\u0000~\u0000\u0006L\u0000\nwhiteSpacet\u0000.Lcom/sun/msv/datatype/xsd/WhiteSpaceProcessor;xpt\u0000\u0000psr\u0000.com.sun.msv.datatype.xsd.WhiteSpaceProcessor$2\u0087z9\u00ee\u00f8,N\u0005\u0002\u0000\u0000xr\u0000,com.sun.msv.datatype.xsd.WhiteSpaceProcessorip\u00ff0C\u00ce\u000eN\u0002\u0000\u0000xp\u0000\u0000sr\u0000$com.sun.msv.datatype.xsd.NmtokenType\u00134\u008c\u00ec!\u00fe+D\u0002\u0000\u0000xr\u0000\"com.sun.msv.datatype.xsd.TokenTypeH\u009b\u00f5!N\u009f\'\u00f6\u0002\u0000\u0000xr\u0000#com.sun.msv.datatype.xsd.StringType\u00c0\t\u00a9y\u00f6\u0011\u009b\u00e6\u0002\u0000\u0000xr\u0000*com.sun.msv.datatype.xsd.BuiltinAtomicType\u0002\u00ff10\u00a8bR\u00ca\u0002\u0000\u0000xr\u0000%com.sun.msv.datatype.xsd.ConcreteType7\u00adsa|\u00d7Z\u001d\u0002\u0000\u0000xq\u0000~\u0000\u0007t\u0000 http://www.w3.org/2001/XMLSchemat\u0000\u0007NMTOKENq\u0000~\u0000\rq\u0000~\u0000\u0013t\u0000\u000benumerationsr\u0000\u0011java.util.HashSet\u00baD\u0085\u0095\u0096\u00b8\u00b74\u0003\u0000\u0000xpw\f\u0000\u0000\u0000\u0010?@\u0000\u0000\u0000\u0000\u0000\u0001t\u0000\u0003anyx");
    private final static org.relaxng.datatype.Datatype ___dt1 = com.sun.xml.bind.unmarshaller.DatatypeDeserializer.deserialize("\u00ac\u00ed\u0000\u0005sr\u0000*com.sun.msv.datatype.xsd.UnsignedShortTypeu\u00b2\u009dc\u00b9\u00c9\f6\u0002\u0000\u0000xr\u0000 com.sun.msv.datatype.xsd.IntType\u00bel\u00b6\u0018\u00b0\u0014\u00e4\u0014\u0002\u0000\u0000xr\u0000+com.sun.msv.datatype.xsd.IntegerDerivedType\u0099\u00f1]\u0090&6k\u00be\u0002\u0000\u0000xr\u0000*com.sun.msv.datatype.xsd.BuiltinAtomicType\u0002\u00ff10\u00a8bR\u00ca\u0002\u0000\u0000xr\u0000%com.sun.msv.datatype.xsd.ConcreteType7\u00adsa|\u00d7Z\u001d\u0002\u0000\u0000xr\u0000\'com.sun.msv.datatype.xsd.XSDatatypeImpl4\u00e9\u0099H.\u00872z\u0002\u0000\u0003L\u0000\fnamespaceUrit\u0000\u0012Ljava/lang/String;L\u0000\btypeNameq\u0000~\u0000\u0006L\u0000\nwhiteSpacet\u0000.Lcom/sun/msv/datatype/xsd/WhiteSpaceProcessor;xpt\u0000 http://www.w3.org/2001/XMLSchemat\u0000\runsignedShortsr\u0000.com.sun.msv.datatype.xsd.WhiteSpaceProcessor$2\u0087z9\u00ee\u00f8,N\u0005\u0002\u0000\u0000xr\u0000,com.sun.msv.datatype.xsd.WhiteSpaceProcessorip\u00ff0C\u00ce\u000eN\u0002\u0000\u0000xp");
    private final static com.sun.msv.grammar.Grammar schemaFragment = com.sun.xml.bind.validator.SchemaDeserializer.deserialize("\u00ac\u00ed\u0000\u0005sr\u0000\u001fcom.sun.msv.grammar.SequenceExp\u0091Ue\u00f1\u0017p\u00f3\u00ad\u0002\u0000\u0000xr\u0000\u001dcom.sun.msv.grammar.BinaryExp\u009f\u008d\u000fi<\u00c1_\u00b7\u0002\u0000\u0002L\u0000\u0004exp1t\u0000 Lcom/sun/msv/grammar/Expression;L\u0000\u0004exp2q\u0000~\u0000\u0002xr\u0000\u001ecom.sun.msv.grammar.Expression\u00f8\u0018\u0082\u00e8N5~O\u0002\u0000\u0003I\u0000\u000ecachedHashCodeL\u0000\u0013epsilonReducibilityt\u0000\u0013Ljava/lang/Boolean;L\u0000\u000bexpandedExpq\u0000~\u0000\u0002xp\u0006\u00ce\u00c1mppsq\u0000~\u0000\u0000\u0004_\u00b6Cppsr\u0000\u001dcom.sun.msv.grammar.ChoiceExp\u00c6|\u00ec3\u0087\u00b8\u00f9\u00f4\u0002\u0000\u0000xq\u0000~\u0000\u0001\u0002\u00b4\u00c0\u00bcppsq\u0000~\u0000\u0007\u0001Z`dppsr\u0000 com.sun.msv.grammar.OneOrMoreExp\u00cc;^\u00bb\u0004E\u00cc\u00db\u0002\u0000\u0000xr\u0000\u001ccom.sun.msv.grammar.UnaryExp\'\u00bbhe^\u001f_5\u0002\u0000\u0001L\u0000\u0003expq\u0000~\u0000\u0002xq\u0000~\u0000\u0003\u0001Z`Ysr\u0000\u0011java.lang.Boolean\u00cd r\u0080\u00d5\u009c\u00fa\u00ee\u0002\u0000\u0001Z\u0000\u0005valuexp\u0000psr\u0000\'com.sun.msv.grammar.trex.ElementPattern\u008b\u0010\u001fsu\u008b\u0091\u00c2\u0002\u0000\u0001L\u0000\tnameClasst\u0000\u001fLcom/sun/msv/grammar/NameClass;xr\u0000\u001ecom.sun.msv.grammar.ElementExp\u00d3\u00c6b\u0095\u0015\rC\u0092\u0002\u0000\u0002Z\u0000\u001aignoreUndeclaredAttributesL\u0000\fcontentModelq\u0000~\u0000\u0002xq\u0000~\u0000\u0003\u0001Z`Vq\u0000~\u0000\u000ep\u0000sq\u0000~\u0000\u000f\u0001Z`Kpp\u0000sq\u0000~\u0000\u0007\u0001Z`@ppsq\u0000~\u0000\n\u0001Z`5q\u0000~\u0000\u000epsr\u0000 com.sun.msv.grammar.AttributeExp0\rR\n\u00c7L\n\u0099\u0002\u0000\u0002L\u0000\u0003expq\u0000~\u0000\u0002L\u0000\tnameClassq\u0000~\u0000\u0010xq\u0000~\u0000\u0003\u0001Z`2q\u0000~\u0000\u000epsr\u00002com.sun.msv.grammar.Expression$AnyStringExpression\u00cb\u00b2\u00f3\u00c9\u0081\u0000\u000f\u00bc\u0002\u0000\u0000xq\u0000~\u0000\u0003\u0000\u0000\u0000\bsq\u0000~\u0000\r\u0001q\u0000~\u0000\u0019sr\u0000 com.sun.msv.grammar.AnyNameClass\u00c3p\u00af\u0013\u00eft\u0094\u00bd\u0002\u0000\u0000xr\u0000\u001dcom.sun.msv.grammar.NameClass\u009c}\u00d4kB\u00c7\tk\u0002\u0000\u0000xpsr\u00000com.sun.msv.grammar.Expression$EpsilonExpression\u0098v\u00e3\u0003Z\u00fex\u00ee\u0002\u0000\u0000xq\u0000~\u0000\u0003\u0000\u0000\u0000\tq\u0000~\u0000\u001apsr\u0000#com.sun.msv.grammar.SimpleNameClass\u0091\u00e9\u00f7\u008d\u00ab\u0010\u00aa\u00f5\u0002\u0000\u0002L\u0000\tlocalNamet\u0000\u0012Ljava/lang/String;L\u0000\fnamespaceURIq\u0000~\u0000!xq\u0000~\u0000\u001ct\u0000\u001dorg.podval.imageio.jaxb.Fieldt\u0000+http://java.sun.com/jaxb/xjc/dummy-elementssq\u0000~\u0000 t\u0000\u0005fieldt\u0000\u0000q\u0000~\u0000\u001fsq\u0000~\u0000\u000f\u0001Z`Vpp\u0000sq\u0000~\u0000\u000f\u0001Z`Kpp\u0000sq\u0000~\u0000\u0007\u0001Z`@ppsq\u0000~\u0000\n\u0001Z`5q\u0000~\u0000\u000epsq\u0000~\u0000\u0016\u0001Z`2q\u0000~\u0000\u000epq\u0000~\u0000\u0019q\u0000~\u0000\u001dq\u0000~\u0000\u001fsq\u0000~\u0000 t\u0000#org.podval.imageio.jaxb.Enumerationq\u0000~\u0000$sq\u0000~\u0000 t\u0000\u000benumerationq\u0000~\u0000\'sq\u0000~\u0000\u0007\u0001\u00aa\u00f5\u0082ppsq\u0000~\u0000\u0016\u0001\u00aa\u00f5wq\u0000~\u0000\u000epsr\u0000\u001bcom.sun.msv.grammar.DataExp8\u00f5\u00f5>{j!\u00cb\u0002\u0000\u0003L\u0000\u0002dtt\u0000\u001fLorg/relaxng/datatype/Datatype;L\u0000\u0006exceptq\u0000~\u0000\u0002L\u0000\u0004namet\u0000\u001dLcom/sun/msv/util/StringPair;xq\u0000~\u0000\u0003\u0001GNUppsr\u0000\"com.sun.msv.datatype.xsd.UnionTypet\u0089\u00d6\u00a7\u0002f\\\t\u0002\u0000\u0001[\u0000\u000bmemberTypest\u0000*[Lcom/sun/msv/datatype/xsd/XSDatatypeImpl;xr\u0000%com.sun.msv.datatype.xsd.ConcreteType7\u00adsa|\u00d7Z\u001d\u0002\u0000\u0000xr\u0000\'com.sun.msv.datatype.xsd.XSDatatypeImpl4\u00e9\u0099H.\u00872z\u0002\u0000\u0003L\u0000\fnamespaceUriq\u0000~\u0000!L\u0000\btypeNameq\u0000~\u0000!L\u0000\nwhiteSpacet\u0000.Lcom/sun/msv/datatype/xsd/WhiteSpaceProcessor;xpq\u0000~\u0000\'t\u0000\u0005Countsr\u0000.com.sun.msv.datatype.xsd.WhiteSpaceProcessor$2\u0087z9\u00ee\u00f8,N\u0005\u0002\u0000\u0000xr\u0000,com.sun.msv.datatype.xsd.WhiteSpaceProcessorip\u00ff0C\u00ce\u000eN\u0002\u0000\u0000xpur\u0000*[Lcom.sun.msv.datatype.xsd.XSDatatypeImpl;H\u001c\u00ad{pzHw\u0002\u0000\u0000xp\u0000\u0000\u0000\u0002sr\u0000*com.sun.msv.datatype.xsd.UnsignedShortTypeu\u00b2\u009dc\u00b9\u00c9\f6\u0002\u0000\u0000xr\u0000 com.sun.msv.datatype.xsd.IntType\u00bel\u00b6\u0018\u00b0\u0014\u00e4\u0014\u0002\u0000\u0000xr\u0000+com.sun.msv.datatype.xsd.IntegerDerivedType\u0099\u00f1]\u0090&6k\u00be\u0002\u0000\u0000xr\u0000*com.sun.msv.datatype.xsd.BuiltinAtomicType\u0002\u00ff10\u00a8bR\u00ca\u0002\u0000\u0000xq\u0000~\u00009t\u0000 http://www.w3.org/2001/XMLSchemat\u0000\runsignedShortq\u0000~\u0000@sr\u0000)com.sun.msv.datatype.xsd.EnumerationFacetp\u008f\u0089\u001d\u0098\u00c5.\u007f\u0002\u0000\u0001L\u0000\u0006valuest\u0000\u000fLjava/util/Set;xr\u00009com.sun.msv.datatype.xsd.DataTypeWithValueConstraintFacet\"\u00a7Ro\u00ca\u00c7\u008aT\u0002\u0000\u0000xr\u0000*com.sun.msv.datatype.xsd.DataTypeWithFacet\u00f4\u00cf\u00e6D\u00b4\u00a8k\u0000\u0002\u0000\u0005Z\u0000\fisFacetFixedZ\u0000\u0012needValueCheckFlagL\u0000\bbaseTypet\u0000)Lcom/sun/msv/datatype/xsd/XSDatatypeImpl;L\u0000\fconcreteTypet\u0000\'Lcom/sun/msv/datatype/xsd/ConcreteType;L\u0000\tfacetNameq\u0000~\u0000!xq\u0000~\u0000:q\u0000~\u0000\'pq\u0000~\u0000@\u0000\u0000sr\u0000$com.sun.msv.datatype.xsd.NmtokenType\u00134\u008c\u00ec!\u00fe+D\u0002\u0000\u0000xr\u0000\"com.sun.msv.datatype.xsd.TokenTypeH\u009b\u00f5!N\u009f\'\u00f6\u0002\u0000\u0000xr\u0000#com.sun.msv.datatype.xsd.StringType\u00c0\t\u00a9y\u00f6\u0011\u009b\u00e6\u0002\u0000\u0000xq\u0000~\u0000Fq\u0000~\u0000Ht\u0000\u0007NMTOKENq\u0000~\u0000@q\u0000~\u0000Tt\u0000\u000benumerationsr\u0000\u0011java.util.HashSet\u00baD\u0085\u0095\u0096\u00b8\u00b74\u0003\u0000\u0000xpw\f\u0000\u0000\u0000\u0010?@\u0000\u0000\u0000\u0000\u0000\u0001t\u0000\u0003anyxsr\u00000com.sun.msv.grammar.Expression$NullSetExpression s\u0080\u0089\u0096\u00cf\u009a@\u0002\u0000\u0000xq\u0000~\u0000\u0003\u0000\u0000\u0000\nq\u0000~\u0000\u000eppsq\u0000~\u0000 t\u0000\u0005countq\u0000~\u0000\'q\u0000~\u0000\u001fsq\u0000~\u0000\u0007\u0002o\u000b%ppsq\u0000~\u0000\u0016\u0002o\u000b\u001aq\u0000~\u0000\u000epsq\u0000~\u00003\u0001\u00fdk\u00fappsr\u0000$com.sun.msv.datatype.xsd.BooleanType\u008f\u0081\u0081\u0090\u00c8\u0002}\u0099\u0002\u0000\u0000xq\u0000~\u0000Fq\u0000~\u0000Ht\u0000\u0007booleanq\u0000~\u0000@q\u0000~\u0000[sr\u0000\u001bcom.sun.msv.util.StringPair\u00d0t\u001ejB\u008f\u008d\u00a0\u0002\u0000\u0002L\u0000\tlocalNameq\u0000~\u0000!L\u0000\fnamespaceURIq\u0000~\u0000!xpq\u0000~\u0000cq\u0000~\u0000Hsq\u0000~\u0000 t\u0000\u0006vectorq\u0000~\u0000\'q\u0000~\u0000\u001fsr\u0000\"com.sun.msv.grammar.ExpressionPool\u00e5\u00f3J;\u00cd]^\u00f8\u0002\u0000\u0001L\u0000\bexpTablet\u0000/Lcom/sun/msv/grammar/ExpressionPool$ClosedHash;xpsr\u0000-com.sun.msv.grammar.ExpressionPool$ClosedHash\u00d7j\u00d0N\u00ef\u00e8\u00ed\u001c\u0002\u0000\u0004I\u0000\u0005countI\u0000\tthresholdL\u0000\u0006parentq\u0000~\u0000i[\u0000\u0005tablet\u0000![Lcom/sun/msv/grammar/Expression;xp\u0000\u0000\u0000\u000b\u0000\u0000\u00009pur\u0000![Lcom.sun.msv.grammar.Expression;\u00d68D\u00c3]\u00ad\u00a7\n\u0002\u0000\u0000xp\u0000\u0000\u0000\u00bfppppq\u0000~\u0000\u0005ppq\u0000~\u0000\u0006ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppq\u0000~\u00001ppppppppppppppppppppppppppppppppppppppppppppppppq\u0000~\u0000\u0015q\u0000~\u0000+ppppppq\u0000~\u0000\bppq\u0000~\u0000\u0014q\u0000~\u0000*pppppppppppppppppppppppq\u0000~\u0000\fppppppppppq\u0000~\u0000\tppppppppppppppppppq\u0000~\u0000^ppppppp");

    public boolean isVector() {
        if (!has_Vector) {
            return com.sun.msv.datatype.xsd.BooleanType.load(com.sun.xml.bind.WhiteSpaceProcessor.collapse("false")).booleanValue();
        } else {
            return _Vector;
        }
    }

    public void setVector(boolean value) {
        _Vector = value;
        has_Vector = true;
    }

    public boolean isSetVector() {
        return has_Vector;
    }

    public void unsetVector() {
        has_Vector = false;
    }

    public org.podval.imageio.jaxb.Enumeration getEnumeration() {
        return _Enumeration;
    }

    public void setEnumeration(org.podval.imageio.jaxb.Enumeration value) {
        _Enumeration = value;
    }

    public java.util.List getFields() {
        return _Fields;
    }

    public Comparable getCount() {
        return _Count;
    }

    public void setCount(Comparable value) {
        _Count = value;
    }

    public com.sun.xml.bind.unmarshaller.ContentHandlerEx getUnmarshaller(com.sun.xml.bind.unmarshaller.UnmarshallingContext context) {
        return new org.podval.imageio.jaxb.impl.RecordImpl.Unmarshaller(context);
    }

    public Class getPrimaryInterfaceClass() {
        return PRIMARY_INTERFACE_CLASS;
    }

    public void serializeElements(com.sun.xml.bind.serializer.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
        int idx3 = 0;
        final int len3 = _Fields.size();
        super.serializeElements(context);
        if ((_Enumeration == null)&&(_Fields.size()>= 1)) {
            while (idx3 != len3) {
                context.startElement("", "field");
                int idx_0 = idx3;
                context.childAsAttributes(((com.sun.xml.bind.serializer.XMLSerializable) _Fields.get(idx_0 ++)));
                context.endAttributes();
                context.childAsElements(((com.sun.xml.bind.serializer.XMLSerializable) _Fields.get(idx3 ++)));
                context.endElement();
            }
        } else {
            if ((_Enumeration!= null)&&(_Fields.size() == 0)) {
                context.startElement("", "enumeration");
                context.childAsAttributes(((com.sun.xml.bind.serializer.XMLSerializable) _Enumeration));
                context.endAttributes();
                context.childAsElements(((com.sun.xml.bind.serializer.XMLSerializable) _Enumeration));
                context.endElement();
            }
        }
    }

    public void serializeAttributes(com.sun.xml.bind.serializer.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
        int idx3 = 0;
        final int len3 = _Fields.size();
        super.serializeAttributes(context);
        if (_Count!= null) {
            context.startAttribute("", "count");
            if (_Count instanceof Integer) {
                context.text(com.sun.msv.datatype.xsd.IntType.save(((Integer) _Count)));
            } else {
                if (_Count instanceof String) {
                    context.text(((String) _Count));
                } else {
                    throw new org.xml.sax.SAXException("type mismatch error");
                }
            }
            context.endAttribute();
        }
        if (has_Vector) {
            context.startAttribute("", "vector");
            context.text(com.sun.msv.datatype.xsd.BooleanType.save(((Boolean) new java.lang.Boolean(_Vector))));
            context.endAttribute();
        }
    }

    public void serializeAttributeBodies(com.sun.xml.bind.serializer.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
        int idx3 = 0;
        final int len3 = _Fields.size();
        super.serializeAttributeBodies(context);
    }

    public Class getPrimaryInterface() {
        return (org.podval.imageio.jaxb.Record.class);
    }

    public com.sun.msv.verifier.DocumentDeclaration createRawValidator() {
        return new com.sun.msv.verifier.regexp.REDocumentDeclaration(schemaFragment);
    }

    public class Unmarshaller
        extends com.sun.xml.bind.unmarshaller.ContentHandlerEx
    {


        public Unmarshaller(com.sun.xml.bind.unmarshaller.UnmarshallingContext context) {
            super(context, "----------");
        }

        protected com.sun.xml.bind.unmarshaller.UnmarshallableObject owner() {
            return org.podval.imageio.jaxb.impl.RecordImpl.this;
        }

        public void enterElement(String ___uri, String ___local, org.xml.sax.Attributes __atts)
            throws com.sun.xml.bind.unmarshaller.UnreportedException
        {
            switch (state) {
                case  2 :
                    if ("".equals(___uri)&&"item".equals(___local)) {
                        _Enumeration = ((org.podval.imageio.jaxb.impl.EnumerationImpl) spawnChildFromEnterElement((org.podval.imageio.jaxb.impl.EnumerationImpl.class), 3, ___uri, ___local, __atts));
                        return ;
                    }
                    break;
                case  1 :
                    if ("".equals(___uri)&&"field".equals(___local)) {
                        context.pushAttributes(__atts);
                        goto8();
                        return ;
                    }
                    if ("".equals(___uri)&&"enumeration".equals(___local)) {
                        context.pushAttributes(__atts);
                        state = 2;
                        return ;
                    }
                    revertToParentFromEnterElement(___uri, ___local, __atts);
                    return ;
            }
            super.enterElement(___uri, ___local, __atts);
        }

        public void leaveElement(String ___uri, String ___local)
            throws com.sun.xml.bind.unmarshaller.UnreportedException
        {
            switch (state) {
                case  9 :
                    if ("".equals(___uri)&&"field".equals(___local)) {
                        context.popAttributes();
                        goto1();
                        return ;
                    }
                    break;
                case  3 :
                    if ("".equals(___uri)&&"enumeration".equals(___local)) {
                        context.popAttributes();
                        goto1();
                        return ;
                    }
                    break;
                case  2 :
                    if ("".equals(___uri)&&"enumeration".equals(___local)) {
                        _Enumeration = ((org.podval.imageio.jaxb.impl.EnumerationImpl) spawnChildFromLeaveElement((org.podval.imageio.jaxb.impl.EnumerationImpl.class), 3, ___uri, ___local));
                        return ;
                    }
                    break;
                case  1 :
                    revertToParentFromLeaveElement(___uri, ___local);
                    return ;
            }
            super.leaveElement(___uri, ___local);
        }

        public void enterAttribute(String ___uri, String ___local)
            throws com.sun.xml.bind.unmarshaller.UnreportedException
        {
            switch (state) {
                case  8 :
                    if ("".equals(___uri)&&"name".equals(___local)) {
                        _Fields.add(((org.podval.imageio.jaxb.impl.FieldImpl) spawnChildFromEnterAttribute((org.podval.imageio.jaxb.impl.FieldImpl.class), 9, ___uri, ___local)));
                        return ;
                    }
                    if ("".equals(___uri)&&"type".equals(___local)) {
                        _Fields.add(((org.podval.imageio.jaxb.impl.FieldImpl) spawnChildFromEnterAttribute((org.podval.imageio.jaxb.impl.FieldImpl.class), 9, ___uri, ___local)));
                        return ;
                    }
                    break;
                case  0 :
                    if ("".equals(___uri)&&"name".equals(___local)) {
                        spawnSuperClassFromEnterAttribute((new org.podval.imageio.jaxb.impl.TypedImpl.Unmarshaller(context)), 1, ___uri, ___local);
                        return ;
                    }
                    if ("".equals(___uri)&&"type".equals(___local)) {
                        spawnSuperClassFromEnterAttribute((new org.podval.imageio.jaxb.impl.TypedImpl.Unmarshaller(context)), 1, ___uri, ___local);
                        return ;
                    }
                    break;
                case  1 :
                    if ("".equals(___uri)&&"count".equals(___local)) {
                        state = 6;
                        return ;
                    }
                    if ("".equals(___uri)&&"vector".equals(___local)) {
                        state = 4;
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
                case  5 :
                    if ("".equals(___uri)&&"vector".equals(___local)) {
                        goto1();
                        return ;
                    }
                    break;
                case  1 :
                    revertToParentFromLeaveAttribute(___uri, ___local);
                    return ;
                case  7 :
                    if ("".equals(___uri)&&"count".equals(___local)) {
                        goto1();
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
                    case  6 :
                        if (___dt0 .isValid(value, context)) {
                            _Count = com.sun.xml.bind.WhiteSpaceProcessor.collapse(value);
                            state = 7;
                            return ;
                        }
                        if (___dt1 .isValid(value, context)) {
                            _Count = com.sun.msv.datatype.xsd.IntType.load(com.sun.xml.bind.WhiteSpaceProcessor.collapse(value));
                            state = 7;
                            return ;
                        }
                        return ;
                    case  1 :
                        revertToParentFromText(value);
                        return ;
                    case  4 :
                        _Vector = com.sun.msv.datatype.xsd.BooleanType.load(com.sun.xml.bind.WhiteSpaceProcessor.collapse(value)).booleanValue();
                        has_Vector = true;
                        state = 5;
                        return ;
                }
            } catch (RuntimeException e) {
                handleUnexpectedTextException(value, e);
            }
        }

        public void leaveChild(int nextState)
            throws com.sun.xml.bind.unmarshaller.UnreportedException
        {
            switch (nextState) {
                case  9 :
                    state = 9;
                    return ;
                case  3 :
                    state = 3;
                    return ;
                case  1 :
                    goto1();
                    return ;
            }
            super.leaveChild(nextState);
        }

        private void goto8()
            throws com.sun.xml.bind.unmarshaller.UnreportedException
        {
            int idx;
            state = 8;
            idx = context.getAttribute("", "type");
            if (idx >= 0) {
                context.consumeAttribute(idx);
                return ;
            }
            idx = context.getAttribute("", "name");
            if (idx >= 0) {
                context.consumeAttribute(idx);
                return ;
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
            idx = context.getAttribute("", "name");
            if (idx >= 0) {
                context.consumeAttribute(idx);
                return ;
            }
        }

        private void goto1()
            throws com.sun.xml.bind.unmarshaller.UnreportedException
        {
            int idx;
            state = 1;
            idx = context.getAttribute("", "count");
            if (idx >= 0) {
                context.consumeAttribute(idx);
                return ;
            }
            idx = context.getAttribute("", "vector");
            if (idx >= 0) {
                context.consumeAttribute(idx);
                return ;
            }
        }

    }

}