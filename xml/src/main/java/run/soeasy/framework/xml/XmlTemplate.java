package run.soeasy.framework.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import lombok.Getter;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.dom.DocumentTemplate;

@Getter
public class XmlTemplate extends DocumentTemplate {
	private final XmlTransformer transformer = new XmlTransformer();
	private final XmlParser parser = new XmlParser();

	public XmlTemplate() {
		getParsers().register(parser);
		getTransformers().register(transformer);
	}

	public Document newDocument(Source source, String rootTagName) {
		Document document = getParser().getDocumentBuilder().newDocument();
		Element rootElement = document.createElement(rootTagName);
		writeTo(source, rootElement);
		document.appendChild(rootElement);
		return document;
	}
}
