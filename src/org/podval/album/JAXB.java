package org.podval.album;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Validator;
import javax.xml.bind.JAXBException;


public class JAXB {

  public static org.podval.album.jaxb.Album
    unmarshallAlbum(File file) throws JAXBException
  {
    return (org.podval.album.jaxb.Album) unmarshall(file);
  }


  public static org.podval.album.jaxb.PictureReferences
    unmarshallPictureReferences(File file) throws JAXBException
  {
    return (org.podval.album.jaxb.PictureReferences) unmarshall(file);
  }


  public static org.podval.album.jaxb.Picture
    unmarshallPicture(File file) throws JAXBException
  {
    return (org.podval.album.jaxb.Picture) unmarshall(file);
  }


  public static Object unmarshall(File file) throws JAXBException {
    return getUnmarshaller().unmarshal(file);
  }


  public static void marshall(Object xml, File file)
    throws FileNotFoundException, IOException, JAXBException
  {
    boolean valid = getValidator().validateRoot(xml);
    FileOutputStream out = new FileOutputStream(file);
    try {
      getMarshaller().marshal(xml, out);
    } finally {
      out.close();
    }
  }


  private static JAXBContext getContext() throws JAXBException {
    if (context == null) {
      context = JAXBContext.newInstance("org.podval.album.jaxb");
    }
    return context;
  }


  private static Unmarshaller getUnmarshaller() throws JAXBException {
    if (unmarshaller == null) {
      unmarshaller = getContext().createUnmarshaller();
      unmarshaller.setValidating(true);
    }
    return unmarshaller;
  }


  private static Marshaller getMarshaller() throws JAXBException {
    if (marshaller == null) {
      marshaller = getContext().createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    }
    return marshaller;
  }


  private static Validator getValidator() throws JAXBException {
    if (validator == null) {
      validator = getContext().createValidator();
    }
    return validator;
  }



  public static org.podval.album.jaxb.ObjectFactory getObjectFactory() {
    if (objectFactory == null) {
      objectFactory = new org.podval.album.jaxb.ObjectFactory();
    }
    return objectFactory;
  }


  private static JAXBContext context;


  private static Unmarshaller unmarshaller;


  private static Marshaller marshaller;


  private static Validator validator;


  private static org.podval.album.jaxb.ObjectFactory objectFactory;
}
