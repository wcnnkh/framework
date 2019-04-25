package scw.beans.property;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.management.openmbean.KeyAlreadyExistsException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.core.utils.ConfigUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;

public final class XmlPropertyUtils {
	private XmlPropertyUtils() {};
	
	public static Map<String, Property> parse(Node rootNode){
		Map<String, Property> map = new HashMap<String, Property>();
		String charset = XMLUtils.getNodeAttributeValue(rootNode, "charset");
		if(StringUtils.isNull(charset)){
			charset = "UTF-8";
		}
		String prefix = XMLUtils.getNodeAttributeValue(rootNode, "prefix");
		
		String file = XMLUtils.getNodeAttributeValue(rootNode, "file");
		if(!StringUtils.isNull(file)){
			File f = ConfigUtils.getFile(file);
			Properties properties = ConfigUtils.getProperties(f, charset);
			for(Entry<Object, Object> entry : properties.entrySet()){
				String name = prefix == null? entry.getKey().toString():prefix + entry.getKey().toString();
				Property property = new Property(name, entry.getValue().toString(), rootNode);
				map.put(property.getName(), property);
			}
		}
		
		NodeList nodeList = rootNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node nRoot = nodeList.item(i);
			if(nRoot == null){
				continue;
			}
			
			if (!"property".equalsIgnoreCase(nRoot.getNodeName())) {
				continue;
			}
			
			Property property = new Property(nRoot, charset);
			String name = prefix == null? property.getName():prefix + property.getName();
			if(map.containsKey(name)){
				throw new KeyAlreadyExistsException(name);
			}
			map.put(name, property);
		}
		return map;
	}
}
