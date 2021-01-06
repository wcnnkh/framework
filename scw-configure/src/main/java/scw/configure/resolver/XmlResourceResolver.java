package scw.configure.resolver;

import scw.convert.ConversionService;
import scw.io.Resource;
import scw.xml.XMLUtils;

public class XmlResourceResolver extends AbstractResourceResolver{
	
	public XmlResourceResolver(ConversionService conversionService) {
		super(conversionService, "*.xml");
	}

	@Override
	protected Object resolve(Resource resource) {
		return XMLUtils.getDocument(resource);
	}

}
