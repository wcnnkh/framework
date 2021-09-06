package io.basc.framework.xml;

import java.util.Map;

import org.w3c.dom.Document;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.dom.DocumentTemplate;

public class XmlTemplate extends DocumentTemplate {
	private final XmlTransformer transformer = new XmlTransformer();
	private final XmlParser parser = new XmlParser();

	public XmlTemplate() {
		parsers.addService(parser);
		transformers.addService(transformer);
	}

	public XmlTransformer getTransformer() {
		return transformer;
	}

	public XmlParser getParser() {
		return parser;
	}

	public Document parse(Object source, TypeDescriptor sourceType) {
		Document document = getParser().getDocumentBuilder().newDocument();
		write(document, document, "xml", source, sourceType);
		return document;
	}

	@SuppressWarnings("rawtypes")
	public Document parse(Map map) {
		return parse(map, TypeDescriptor.map(Map.class, Object.class, Object.class));
	}

	@SuppressWarnings("rawtypes")
	public String toString(Map map) {
		Document document = parse(map);
		return toString(document);
	}
}
