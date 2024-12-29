package io.basc.framework.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import io.basc.framework.beans.factory.spi.SPI;
import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.config.ConversionService;
import io.basc.framework.core.env.SystemProperties;
import io.basc.framework.dom.DocumentParser;
import io.basc.framework.dom.DomException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Pipeline;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.io.Resource;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

public class XmlParser implements DocumentParser, ConversionService {
	private static Logger logger = LogManager.getLogger(XmlParser.class);
	private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
	@Nullable
	private static final EntityResolver ENTITY_RESOLVER = SPI.getServices(EntityResolver.class).first();

	static {
		DOCUMENT_BUILDER_FACTORY.setIgnoringElementContentWhitespace(SystemProperties.getInstance()
				.get("io.basc.framework.xml.ignoring.element.content.whitespace").or(true).getAsBoolean());
		DOCUMENT_BUILDER_FACTORY.setIgnoringComments(
				SystemProperties.getInstance().get("io.basc.framework.xml.ignoring.comments").or(true).getAsBoolean());
		DOCUMENT_BUILDER_FACTORY.setCoalescing(
				SystemProperties.getInstance().get("io.basc.framework.dom.coalescing").or(true).getAsBoolean());
		DOCUMENT_BUILDER_FACTORY.setExpandEntityReferences(
				SystemProperties.getInstance().getAsBoolean("io.basc.framework.xml.expand.entity.references"));
		DOCUMENT_BUILDER_FACTORY.setNamespaceAware(
				SystemProperties.getInstance().getAsBoolean("io.basc.framework.xml.namespace.aware"));
		try {
			DOCUMENT_BUILDER_FACTORY.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		} catch (ParserConfigurationException e) {
			logger.warn(e, "config document builder factory error!");
		}
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
			return IgnoreDTDResolver.INSTANCE;
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
	public <T, E extends Throwable> T parse(Resource resource,
			Pipeline<? super Document, ? extends T, ? extends E> processor) throws IOException, DomException, E {
		return resource.read((is) -> {
			Document document = parse(is);
			if (document == null) {
				return null;
			}

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

	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return Document.class.isAssignableFrom(targetType.getType())
				&& (InputStream.class.isAssignableFrom(sourceType.getType())
						|| Reader.class.isAssignableFrom(sourceType.getType())
						|| String.class.isAssignableFrom(sourceType.getType())
						|| InputSource.class.isAssignableFrom(sourceType.getType())
						|| File.class.isAssignableFrom(sourceType.getType()));
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (InputStream.class.isAssignableFrom(sourceType.getType())) {
			return XmlUtils.getTemplate().getParser().parse((InputStream) source);
		} else if (Reader.class.isAssignableFrom(sourceType.getType())) {
			return XmlUtils.getTemplate().getParser().parse((Reader) source);
		} else if (String.class.isAssignableFrom(sourceType.getType())) {
			return XmlUtils.getTemplate().getParser().parse((String) source);
		} else if (InputSource.class.isAssignableFrom(sourceType.getType())) {
			return XmlUtils.getTemplate().getParser().parse((InputSource) source);
		} else if (File.class.isAssignableFrom(sourceType.getType())) {
			return XmlUtils.getTemplate().getParser().parse((File) source);
		}
		throw new ConversionException(sourceType.toString());
	}
}
