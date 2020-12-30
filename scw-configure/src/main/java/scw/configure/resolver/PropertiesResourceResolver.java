package scw.configure.resolver;

import java.io.IOException;
import java.util.Properties;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.io.Resource;
import scw.io.ResourceUtils;

public class PropertiesResourceResolver extends AbstractResourceResolver{
	private String charsetName;
	
	public PropertiesResourceResolver(ConversionService conversionService, String charsetName){
		super(conversionService);
		this.charsetName = charsetName;
	}
	
	public boolean matches(Resource resource, TypeDescriptor targetType) {
		return resource.exists() && resource.getFilename().endsWith(".properties");
	}

	@Override
	protected Object resolve(Resource resource) throws IOException {
		Properties properties = new Properties();
		ResourceUtils.loadProperties(properties, resource, charsetName);
		return properties;
	}
}
