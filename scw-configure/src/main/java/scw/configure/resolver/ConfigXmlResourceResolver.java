package scw.configure.resolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.convert.ConversionService;
import scw.io.Resource;
import scw.xml.XMLUtils;

public class ConfigXmlResourceResolver extends AbstractXmlResourceResolver{
	private final String rootTag;
	
	public ConfigXmlResourceResolver(ConversionService conversionService, String rootTag){
		super(conversionService);
		this.rootTag = rootTag;
	}
	
	@Override
	protected Object resolveXml(Resource resource, Document document)
			throws IOException {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Element root = document.getDocumentElement();
		NodeList nhosts = root.getChildNodes();
		for (int x = 0; x < nhosts.getLength(); x++) {
			Node nRoot = nhosts.item(x);
			if (nRoot.getNodeName().equalsIgnoreCase(rootTag)) { 
				list.add(XMLUtils.xmlToMap(nRoot));
			}
		}
		return list;
	}
}
