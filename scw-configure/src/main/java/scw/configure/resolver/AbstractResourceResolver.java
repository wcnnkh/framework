package scw.configure.resolver;

import java.io.IOException;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.io.Resource;

public abstract class AbstractResourceResolver implements ResourceResolver{
	private final ConversionService conversionService;
	
	public AbstractResourceResolver(ConversionService conversionService){
		this.conversionService = conversionService;
	}
	
	public ConversionService getConversionService() {
		return conversionService;
	}

	public Object resolve(Resource resource, TypeDescriptor targetType)
			throws IOException {
		if(!resource.exists()){
			return null;
		}

		Object source = resolve(resource);
		if(targetType.getType() == Object.class){
			return source;
		}
		
		return conversionService.convert(source, TypeDescriptor.forObject(source), targetType);
	}
	
	protected abstract Object resolve(Resource resource) throws IOException;

}
