package io.basc.framework.dom;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.dom.append.AppendChildService;
import io.basc.framework.dom.append.AppendChildServiceFactory;
import io.basc.framework.dom.append.DefaultAppendChildServiceFactory;
import io.basc.framework.env.Sys;
import io.basc.framework.io.IOUtils;
import io.basc.framework.io.Resource;
import io.basc.framework.lang.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DomBuilder {
	@Nullable
	private static final EntityResolver ENTITY_RESOLVER = Sys.env.getServiceLoader(EntityResolver.class).first();
	private static final AppendChildServiceFactory APPEND_CHILD_SERVICE_FACTORY = new DefaultAppendChildServiceFactory();

	static {
		APPEND_CHILD_SERVICE_FACTORY.getServices().addAll(Sys.env.getServiceLoader(AppendChildService.class).toList());
	}

	private final DocumentBuilderFactory documentBuilderFactory;
	private final TransformerFactory transformerFactory;
	private final AppendChildService appendChildService;
	private EntityResolver entityResolver = ENTITY_RESOLVER;

	public DomBuilder(DocumentBuilderFactory documentBuilderFactory, TransformerFactory transformerFactory) {
		this(documentBuilderFactory, transformerFactory, APPEND_CHILD_SERVICE_FACTORY);
	}

	public DomBuilder(DocumentBuilderFactory documentBuilderFactory, TransformerFactory transformerFactory,
			AppendChildService appendChildService) {
		this.documentBuilderFactory = documentBuilderFactory;
		this.transformerFactory = transformerFactory;
		this.appendChildService = appendChildService;
	}

	public EntityResolver getEntityResolver() {
		if (entityResolver == null) {
			return DefaultEntityResolver.INSTANCE;
		}
		return entityResolver;
	}

	public void setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
	}

	public DocumentBuilderFactory getDocumentBuilderFactory() {
		return documentBuilderFactory;
	}

	public TransformerFactory getTransformerFactory() {
		return transformerFactory;
	}

	public DocumentBuilder getDocumentBuilder() {
		DocumentBuilder documentBuilder;
		try {
			documentBuilder = getDocumentBuilderFactory().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new DomException(e);
		}

		EntityResolver entityResolver = getEntityResolver();
		if (entityResolver != null) {
			documentBuilder.setEntityResolver(entityResolver);
		}
		return documentBuilder;
	}

	public Transformer getTransformer() {
		try {
			return getTransformerFactory().newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public Document parse(InputSource source) {
		try {
			return getDocumentBuilder().parse(source);
		} catch (SAXException e) {
			throw new ConversionException(source.toString(), e);
		} catch (IOException e) {
			throw new ConversionException(source.toString(), e);
		}
	}

	public Document parse(InputStream source) {
		return parse(source, null);
	}

	protected Document parse(InputStream source, @Nullable String systemId) {
		try {
			return systemId == null ? getDocumentBuilder().parse(source) : getDocumentBuilder().parse(source, systemId);
		} catch (SAXException e) {
			throw new ConversionException(source.toString(), e);
		} catch (IOException e) {
			throw new ConversionException(source.toString(), e);
		}
	}

	public Document parse(Reader source) {
		return parse(source, null);
	}

	public Document parse(Reader source, String systemId) {
		InputSource inputSource = new InputSource(source);
		if (systemId != null) {
			inputSource.setSystemId(systemId);
		}
		return parse(inputSource);
	}

	public Document parse(String source) {
		return parse(source, null);
	}

	public Document parse(String source, String systemId) {
		StringReader stringReader = new StringReader(source);
		try {
			return parse(stringReader, systemId);
		} finally {
			IOUtils.closeQuietly(stringReader);
		}
	}

	public Document parse(File file) {
		try {
			return getDocumentBuilder().parse(file);
		} catch (SAXException e) {
			throw new ConversionException(file.toString(), e);
		} catch (IOException e) {
			throw new ConversionException(file.toString(), e);
		}
	}

	public Document parse(Resource resource) {
		return parse(resource, null);
	}

	public Document parse(Resource resource, String systemId) {
		InputStream is = null;
		try {
			is = resource.getInputStream();
			return parse(is, systemId);
		} catch (IOException e) {
			throw new RuntimeException(resource.getDescription(), e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	@SuppressWarnings("rawtypes")
	public Document parse(Map source) {
		Document document = getDocumentBuilder().newDocument();
		append(document, "xml", source);
		return document;
	}
	
	public void append(Document document, String rootNodeName, Object source){
		getAppendChildService().append(document, document, rootNodeName, source, TypeDescriptor.forObject(source));
	}

	public AppendChildService getAppendChildService() {
		return appendChildService;
	}

	/***** transform *****/

	@SuppressWarnings("rawtypes")
	public String toString(Map source) {
		return toString(parse(source));
	}

	public String toString(Node node) {
		return toString(node, null);
	}

	public String toString(Node node, String systemID) {
		return toString(getTransformer(), node, systemID);
	}

	public String toString(Transformer transformer, Node node) {
		return toString(transformer, node, null);
	}

	public String toString(Transformer transformer, Node node, String systemID) {
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource domSource = new DOMSource(node);
		if (systemID != null) {
			domSource.setSystemId(systemID);
		}
		String content = null;
		try {
			transform(transformer, domSource, result);
			content = sw.toString();
		} finally {
			IOUtils.closeQuietly(sw);
		}
		return content;
	}

	public void transform(Source xmlSource, File output) {
		transform(getTransformer(), xmlSource, output);
	}

	public void transform(Transformer transformer, Source xmlSource, File output) {
		StreamResult result = new StreamResult(output);
		transform(transformer, xmlSource, result);
	}

	public void transform(Source xmlSource, Writer writer) {
		transform(getTransformer(), xmlSource, writer);
	}

	public void transform(Transformer transformer, Source xmlSource, Writer writer) {
		StreamResult result = new StreamResult(writer);
		transform(transformer, xmlSource, result);
	}

	public void transform(Source xmlSource, OutputStream outputStream) {
		transform(getTransformer(), xmlSource, outputStream);
	}

	public void transform(Transformer transformer, Source xmlSource, OutputStream outputStream) {
		StreamResult result = new StreamResult(outputStream);
		transform(transformer, xmlSource, result);
	}

	public void transform(Source xmlSource, Result outputTarget) {
		transform(getTransformer(), xmlSource, outputTarget);
	}

	public void transform(Transformer transformer, Source xmlSource, Result outputTarget) {
		try {
			transformer.transform(xmlSource, outputTarget);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}
}
