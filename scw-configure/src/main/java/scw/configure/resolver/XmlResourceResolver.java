package scw.configure.resolver;

import scw.convert.ConversionService;
import scw.dom.DomUtils;
import scw.io.Resource;

public class XmlResourceResolver extends AbstractResourceResolver{
	
	public XmlResourceResolver(ConversionService conversionService) {
		super(conversionService, "*.xml");
	}

	@Override
	protected Object resolve(Resource resource) {
		return DomUtils.getDomBuilder().parse(resource);
	}

}
