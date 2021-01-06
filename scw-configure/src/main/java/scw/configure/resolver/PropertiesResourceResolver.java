package scw.configure.resolver;

import java.util.Properties;

import scw.convert.ConversionService;
import scw.io.Resource;
import scw.io.ResourceUtils;

public class PropertiesResourceResolver extends AbstractResourceResolver{
	private String charsetName;
	
	public PropertiesResourceResolver(ConversionService conversionService, String charsetName){
		super(conversionService, "*.properties");
		this.charsetName = charsetName;
	}

	@Override
	protected Object resolve(Resource resource) {
		Properties properties = new Properties();
		ResourceUtils.loadProperties(properties, resource, charsetName);
		return properties;
	}
}
