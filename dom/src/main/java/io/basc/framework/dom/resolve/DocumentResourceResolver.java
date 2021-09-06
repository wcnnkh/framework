package io.basc.framework.dom.resolve;

import org.w3c.dom.Document;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.resolve.ResourceResolver;
import io.basc.framework.dom.DomUtils;
import io.basc.framework.io.Resource;

public class DocumentResourceResolver implements ResourceResolver, ConversionServiceAware {
	private ConversionService conversionService;

	public ConversionService getConversionService() {
		return conversionService;
	}

	@Override
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public boolean canResolveResource(Resource resource, TypeDescriptor targetType) {
		return DomUtils.getTemplate().canParse(resource)
				&& getConversionService().canConvert(TypeDescriptor.valueOf(Document.class), targetType);
	}

	public Object resolveResource(Resource resource, TypeDescriptor targetType) {
		return DomUtils.getTemplate().parse(resource,
				(document) -> getConversionService().convert(document, TypeDescriptor.forObject(document), targetType));
	}
}
