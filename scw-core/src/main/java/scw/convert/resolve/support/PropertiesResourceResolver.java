package scw.convert.resolve.support;

import java.nio.charset.Charset;
import java.util.Properties;
import java.util.function.Supplier;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.io.Resource;
import scw.io.resolver.PropertiesResolver;

public class PropertiesResourceResolver extends AbstractResourceResolver {
	private static final TypeDescriptor PROPERTIES_TYPE = TypeDescriptor.valueOf(Properties.class);
	private final PropertiesResolver propertiesResolver;
	private final Supplier<Charset> charset;
	
	public PropertiesResourceResolver(ConversionService conversionService, PropertiesResolver propertiesResolver, Supplier<Charset> charset) {
		super(conversionService);
		this.propertiesResolver = propertiesResolver;
		this.charset = charset;
	}

	public boolean canResolveResource(Resource resource,
			TypeDescriptor targetType) {
		return propertiesResolver.canResolveProperties(resource) && getConversionService().canConvert(PROPERTIES_TYPE, targetType);
	}
	
	public Object resolveResource(Resource resource, TypeDescriptor targetType) {
		Properties properties = new Properties();
		propertiesResolver.resolveProperties(properties, resource, charset.get());
		return getConversionService().convert(properties, PROPERTIES_TYPE, targetType);
	}
}
