package scw.convert.resolve.support;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.convert.resolve.ResourceResolver;
import scw.dom.DomBuilder;
import scw.dom.DomUtils;
import scw.io.Resource;

public abstract class AbstractResourceResolver implements ResourceResolver{
	static final TypeDescriptor RESOURCE_TYPE = TypeDescriptor.valueOf(Resource.class);
	private final ConversionService conversionService;
	private DomBuilder domBuilder;
	
	public AbstractResourceResolver(ConversionService conversionService){
		this.conversionService = conversionService;
	}
	
	public DomBuilder getDomBuilder() {
		return domBuilder == null? DomUtils.getDomBuilder():domBuilder;
	}

	public void setDomBuilder(DomBuilder domBuilder) {
		this.domBuilder = domBuilder;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}
}
