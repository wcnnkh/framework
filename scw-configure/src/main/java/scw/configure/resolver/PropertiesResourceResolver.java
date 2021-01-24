package scw.configure.resolver;

import java.nio.charset.Charset;
import java.util.Properties;

import scw.convert.ConversionService;
import scw.io.Resource;
import scw.io.resolver.support.DefaultPropertiesResolver;
import scw.lang.Nullable;

public class PropertiesResourceResolver extends AbstractResourceResolver{
	private Charset charset;
	
	public PropertiesResourceResolver(ConversionService conversionService, @Nullable Charset charset){
		super(conversionService, "*.properties");
		this.charset = charset;
	}

	@Override
	protected Object resolve(Resource resource) {
		Properties properties = new Properties();
		DefaultPropertiesResolver.INSTANCE.resolveProperties(properties, resource, charset);
		return properties;
	}
}
