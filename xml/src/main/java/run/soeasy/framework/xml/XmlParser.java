package run.soeasy.framework.xml;

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
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.ConverterNotFoundException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.dom.DomException;
import run.soeasy.framework.dom.resource.ResourceParser;
import run.soeasy.framework.io.Resource;

@RequiredArgsConstructor
@Getter
@Setter
public class XmlParser implements ResourceParser, Converter {
	@NonNull
	private DocumentBuilderFactory documentBuilderFactory;
	@NonNull
	private EntityResolver entityResolver = new IgnoreDTDResolver();

	public XmlParser() {
		this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
		this.documentBuilderFactory.setIgnoringElementContentWhitespace(true);
		this.documentBuilderFactory.setIgnoringComments(true);
		this.documentBuilderFactory.setCoalescing(true);
		this.documentBuilderFactory.setExpandEntityReferences(false);
		this.documentBuilderFactory.setNamespaceAware(false);
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
			ThrowingFunction<? super Node, ? extends T, ? extends E> processor) throws IOException, E {
		return resource.getInputStreamPipeline().optional().flatMap((is) -> {
			Document document = parse(is);
			if (document == null) {
				return null;
			}

			return processor.apply(document.getDocumentElement());
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

	@Override
	public Object convert(Object source, @NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType)
			throws ConversionException {
		if (source instanceof InputStream) {
			return parse((InputStream) source);
		} else if (Reader.class.isAssignableFrom(sourceType.getType())) {
			return parse((Reader) source);
		} else if (String.class.isAssignableFrom(sourceType.getType())) {
			return parse((String) source);
		} else if (InputSource.class.isAssignableFrom(sourceType.getType())) {
			return parse((InputSource) source);
		} else if (File.class.isAssignableFrom(sourceType.getType())) {
			return parse((File) source);
		}
		throw new ConverterNotFoundException(sourceType, targetType);
	}
}
