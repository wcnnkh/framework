package scw.common.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLUtils {
	private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
	private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
	
	public static DocumentBuilder getDocumentBuilder(){
		try {
			return DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Document newDocument(){
		return getDocumentBuilder().newDocument();
	}
	
	public static Transformer getTransformer(){
		try {
			return TRANSFORMER_FACTORY.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Document parse(InputStream is){
		DocumentBuilder documentBuilder = getDocumentBuilder();
		try {
			return documentBuilder.parse(is);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Document parse(File file){
		DocumentBuilder documentBuilder = getDocumentBuilder();
		try {
			return documentBuilder.parse(file);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Document parse(InputSource is){
		DocumentBuilder documentBuilder = getDocumentBuilder();
		try {
			return documentBuilder.parse(is);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Document parseForURI(String uri){
		DocumentBuilder documentBuilder = getDocumentBuilder();
		try {
			return documentBuilder.parse(uri);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Document parse(InputStream is, String systemId){
		DocumentBuilder documentBuilder = getDocumentBuilder();
		try {
			return documentBuilder.parse(is, systemId);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Document parse(String text){
		return parse(new InputSource(new StringReader(text)));
	}
	
	
	public static String getXmlContent(Node node){
		Transformer transformer;
		try {
			transformer = TRANSFORMER_FACTORY.newTransformer();
		} catch (TransformerConfigurationException e1) {
			e1.printStackTrace();
			return null;
		}
		
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource domSource = new DOMSource(node);
		String content = null;
		try {
			transformer.transform(domSource, result);
			content = sw.toString();
		} catch (TransformerException e) {
			e.printStackTrace();
		}finally {
			XUtils.close(sw);
		}
		return content;
	}
	
	public static Map<String, String> xmlToMap(Node node){
		NodeList nodeList = node.getChildNodes();
		Map<String, String> map = new HashMap<String, String>();
		for(int i=0; i<nodeList.getLength(); i++){
			Node n = nodeList.item(i);
			if(n == null){
				continue;
			}
			
			String nodeKey = n.getNodeName().intern();
			String value = n.getTextContent();
			if(nodeKey == null || nodeKey.length() == 0 || "#text".equals(nodeKey)){
				continue;
			}
			map.put(nodeKey, value);
		}
		return map;
	}
	
	public static String mapToXml(Map<String, String> map){
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
		Document document = documentBuilder.newDocument();
		Element root = document.createElement("xml");
		for(Entry<String, String> entry : map.entrySet()){
			if(entry.getKey() == null || entry.getValue() == null){
				continue;
			}
			
			Element element = document.createElement(entry.getKey());
			element.setTextContent(entry.getValue());
			root.appendChild(element);
		}
		return getXmlContent(root);
	}
}
