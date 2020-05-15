package scw.net.http.server.mvc.parameter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import scw.beans.annotation.Bean;
import scw.net.http.server.ServerHttpRequest;
import scw.xml.XMLUtils;

@Bean(singleton = false)
public final class XmlMap extends LinkedHashMap<String, String> {
	private static final long serialVersionUID = 1L;

	public XmlMap(ServerHttpRequest request) throws IOException {
		BufferedReader reader = request.getReader();
		Document document = XMLUtils.parse(new InputSource(reader));
		Element element = document.getDocumentElement();
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);
			if (XMLUtils.ignoreNode(n)) {
				continue;
			}

			put(n.getNodeName(), n.getTextContent());
		}
	}
}
