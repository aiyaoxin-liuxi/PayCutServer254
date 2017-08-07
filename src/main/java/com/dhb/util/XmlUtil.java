package com.dhb.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.dhb.entity.form.InBEDC;
public class XmlUtil {
	public static String findValueByXpath(String context,String key){
		 XPathFactory  factory =XPathFactory.newInstance();
		 XPath xPath = factory.newXPath();
		   try {
			return xPath.evaluate(key, new InputSource(new StringReader(context)));
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static Element parseXml(String xmlString,String encode) throws ParserConfigurationException, SAXException, IOException{
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();		      
	    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();			
		org.w3c.dom.Document doc = docBuilder.parse(new ByteArrayInputStream(xmlString.getBytes(encode)));
		DOMReader reader = new DOMReader();
		Document document = reader.read(doc);
		Element element = document.getRootElement();
		return element;
}
	
	public static Object xmltoObject(String xml ,@SuppressWarnings("rawtypes") Class className)throws Exception{
		StringReader reader = new StringReader(xml);
		 JAXBContext jaxbContext = JAXBContext.newInstance(className);
		 Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		 Object object = jaxbUnmarshaller.unmarshal(reader);
		 reader.close();
		 return object;
	}
	
	public static String ObjectToXml(Object object){
		return ObjectToXml(object,"UTF-8");
	}
	public static String ObjectToXml(Object object,String encodeType){
		String xml = null;
		StringWriter writer = new StringWriter();
		 try {
			   JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
			   Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			   jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING,encodeType);
			   jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			   jaxbMarshaller.marshal(object,writer);
			   xml =new String(writer.getBuffer());
			  return xml;
			  } catch (JAXBException e) {
			   e.printStackTrace();
			  }finally{
				  try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  }
		return null;
	}
	public static void main(String[] args) {
		InBEDC bedc = new InBEDC();
		String xml=XmlUtil.ObjectToXml(bedc);
		System.out.println(xml);
	}

}
