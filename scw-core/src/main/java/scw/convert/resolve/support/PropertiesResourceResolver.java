package scw.convert.resolve.support;

import java.nio.charset.Charset;
import java.util.Properties;
import java.util.function.Supplier;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.convert.resolve.ResourceResolver;
import scw.io.Resource;
import scw.io.resolver.PropertiesResolver;

public class PropertiesResourceResolver implements ResourceResolver {
	private static final TypeDescriptor PROPERTIES_TYPE = TypeDescriptor.valueOf(Properties.class);
	private final PropertiesResolver propertiesResolver;
	private final Supplier<Charset> charset;
	private final ConversionService conversionService;

	public PropertiesResourceResolver(ConversionService conversionService, PropertiesResolver propertiesResolver,
			Supplier<Charset> charset) {
		this.conversionService = conversionService;
		this.propertiesResolver = propertiesResolver;
		this.charset = charset;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public boolean canResolveResource(Resource resource, TypeDescriptor targetType) {
		return propertiesResolver.canResolveProperties(resource)
				&& getConversionService().canConvert(PROPERTIES_TYPE, targetType);
	}

	public Object resolveResource(Resource resource, TypeDescriptor targetType) {
		Properties properties = new Properties();
		propertiesResolver.resolveProperties(properties, resource, charset.get());
		return getConversionService().convert(properties, PROPERTIES_TYPE, targetType);
	}
}
