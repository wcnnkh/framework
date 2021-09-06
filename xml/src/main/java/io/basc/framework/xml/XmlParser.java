package io.basc.framework.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import io.basc.framework.dom.DefaultEntityResolver;
import io.basc.framework.dom.DocumentParser;
import io.basc.framework.dom.DomException;
import io.basc.framework.env.Sys;
import io.basc.framework.io.Resource;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.stream.Processor;

public class XmlParser implements DocumentParser {
	private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
	@Nullable
	private static final EntityResolver ENTITY_RESOLVER = Sys.env.getServiceLoader(EntityResolver.class).first();

	static {
		DOCUMENT_BUILDER_FACTORY.setIgnoringElementContentWhitespace(
				Sys.env.getValue("io.basc.framework.xml.ignoring.element.content.whitespace", boolean.class, true));
		DOCUMENT_BUILDER_FACTORY
				.setIgnoringComments(Sys.env.getValue("io.basc.framework.xml.ignoring.comments", boolean.class, true));
		DOCUMENT_BUILDER_FACTORY
				.setCoalescing(Sys.env.getValue("io.basc.framework.dom.coalescing", boolean.class, true));
		DOCUMENT_BUILDER_FACTORY.setExpandEntityReferences(
				Sys.env.getValue("io.basc.framework.xml.expand.entity.references", boolean.class, false));
		DOCUMENT_BUILDER_FACTORY
				.setNamespaceAware(Sys.env.getValue("io.basc.framework.xml.namespace.aware", boolean.class, false));
	}

	private final DocumentBuilderFactory documentBuilderFactory;
	private EntityResolver entityResolver;

	public XmlParser() {
		this(DOCUMENT_BUILDER_FACTORY);
	}

	public XmlParser(DocumentBuilderFactory documentBuilderFactory) {
		Assert.requiredArgument(documentBuilderFactory != null, "documentBuilderFactory");
		this.documentBuilderFactory = documentBuilderFactory;
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

	public DocumentBuilder getDocumentBuilder() {
		DocumentBuilder documentBuilder;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new DomException(e);
		}
		EntityResolver entityResolver = getEntityResolver();
		if (entityResolver != null) {
			documentBuilder.setEntityResolver(entityResolver);
		}
		return documentBuilder;
	}

	@Override
	public boolean canParse(Resource resource) {
		return resource != null && resource.exists() && resource.getName().endsWith(".xml");
	}

	@Override
	public <T, E extends Throwable> T parse(Resource resource, Processor<Document, ? extends T, E> processor)
			throws IOException, DomException {
		return resource.read((is) -> {
			Document document = parse(is);
			try {
				return processor.process(document);
			} catch (Throwable e) {
				throw new DomException(e);
			}
		});
	}

	public Document parse(InputSource source) {
		if (source == null) {
			return null;
		}

		try {
			return getDocumentBuilder().parse(source);
		} catch (SAXException | IOException e) {
			throw new DomException(e);
		}
	}

	public Document parse(Reader source) {
		if (source == null) {
			return null;
		}

		try {
			return getDocumentBuilder().parse(new InputSource(source));
		} catch (SAXException | IOException e) {
			throw new DomException(e);
		}
	}

	public Document parse(InputStream source) {
		if (source == null) {
			return null;
		}

		try {
			return getDocumentBuilder().parse(source);
		} catch (SAXException | IOException e) {
			throw new DomException(e);
		}
	}

	public Document parse(File source) {
		if (source == null) {
			return null;
		}

		try {
			return getDocumentBuilder().parse(source);
		} catch (SAXException | IOException e) {
			throw new DomException(e);
		}
	}

	public Document parse(URI source) {
		if (source == null) {
			return null;
		}

		try {
			return getDocumentBuilder().parse(source.toASCIIString());
		} catch (SAXException | IOException e) {
			throw new DomException(e);
		}
	}

	public Document parse(String content) {
		if (StringUtils.isEmpty(content)) {
			return null;
		}

		return parse(new InputSource(new StringReader(content)));
	}
}
