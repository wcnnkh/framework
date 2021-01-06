package scw.configure.resolver;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.io.Resource;
import scw.util.DefaultStringMatcher;

public abstract class AbstractResourceResolver implements ResourceResolver{
	private final ConversionService conversionService;
	private final String fileNamePattern;
	
	public AbstractResourceResolver(ConversionService conversionService, String fileNamePattern){
		this.conversionService = conversionService;
		this.fileNamePattern = fileNamePattern;
	}
	
	public ConversionService getConversionService() {
		return conversionService;
	}
	
	public boolean matches(Resource resource, TypeDescriptor targetType) {
		return resource.exists() && DefaultStringMatcher.getInstance().match(fileNamePattern, resource.getFilename());
	}

	public Object resolve(Resource resource, TypeDescriptor targetType) {
		if(!resource.exists()){
			return null;
		}

		Object source = resolve(resource);
		if(targetType.getType() == Object.class){
			return source;
		}
		return conversionService.convert(source, getSourceTypeDescriptor(resource, source), targetType);
	}
	
	protected TypeDescriptor getSourceTypeDescriptor(Resource resource, Object source){
		return TypeDescriptor.forObject(source);
	}
	
	protected abstract Object resolve(Resource resource);

}
