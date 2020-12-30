package scw.configure.resolver;

import java.io.IOException;

import org.w3c.dom.Document;

import scw.convert.ConversionService;
import scw.io.Resource;
import scw.xml.XMLUtils;

public class XmlResourceResolver extends AbstractXmlResourceResolver {

	public XmlResourceResolver(ConversionService conversionService) {
		super(conversionService);
	}

	@Override
	protected Object resolveXml(Resource resource, Document document)
			throws IOException {
		return XMLUtils.toRecursionMap(document.getDocumentElement());
	}
}
