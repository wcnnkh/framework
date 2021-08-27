package io.basc.framework.dom.convert;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.resolve.ResourceResolver;
import io.basc.framework.dom.DomBuilder;
import io.basc.framework.dom.DomUtils;
import io.basc.framework.io.Resource;

import org.w3c.dom.Document;

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
