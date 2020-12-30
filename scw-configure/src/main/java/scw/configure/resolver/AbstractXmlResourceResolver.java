package scw.configure.resolver;

import java.io.IOException;
import java.io.InputStream;

import org.w3c.dom.Document;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.io.IOUtils;
import scw.io.Resource;
import scw.xml.XMLUtils;

public abstract class AbstractXmlResourceResolver extends AbstractResourceResolver {
	public static final String XML_SUFFIX = ".xml";
	
	public AbstractXmlResourceResolver(ConversionService conversionService) {
		super(conversionService);
	}

	public boolean matches(Resource resource, TypeDescriptor targetType) {
		return resource.exists() && resource.getFilename().endsWith(XML_SUFFIX);
	}

	protected Object resolve(Resource resource) throws IOException {
		InputStream inputStream = null;
		try {
			inputStream = resource.getInputStream();
			Document document = XMLUtils.parse(inputStream);
			return resolveXml(resource, document);
		} finally {
			IOUtils.close(inputStream);
		}
	}

	protected abstract Object resolveXml(Resource resource, Document document)
			throws IOException;
}
