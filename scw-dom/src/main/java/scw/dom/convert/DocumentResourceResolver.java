package scw.dom.convert;

import org.w3c.dom.Document;

import scw.convert.ConversionService;
import scw.convert.ConversionServiceAware;
import scw.convert.TypeDescriptor;
import scw.convert.resolve.ResourceResolver;
import scw.dom.DomBuilder;
import scw.dom.DomUtils;
import scw.io.Resource;

public class DocumentResourceResolver implements ResourceResolver, ConversionServiceAware {
	private DomBuilder domBuilder;
	private ConversionService conversionService;

	public ConversionService getConversionService() {
		return conversionService;
	}

	@Override
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public DomBuilder getDomBuilder() {
		return domBuilder == null ? DomUtils.getDomBuilder() : domBuilder;
	}

	public void setDomBuilder(DomBuilder domBuilder) {
		this.domBuilder = domBuilder;
	}

	public boolean canResolveResource(Resource resource, TypeDescriptor targetType) {
		return resource.exists() && resource.getName().endsWith(".xml");
	}

	public Object resolveResource(Resource resource, TypeDescriptor targetType) {
		Document document = getDomBuilder().parse(resource);
		if (document == null) {
			return null;
		}
		return getConversionService().convert(document, TypeDescriptor.forObject(document), targetType);
	}
}
