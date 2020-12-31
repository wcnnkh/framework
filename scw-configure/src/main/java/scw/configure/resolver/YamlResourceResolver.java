package scw.configure.resolver;

import java.io.IOException;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.io.Resource;
import scw.yaml.YamlProperties;

/*
 * 需要引入scw-yaml模块
 */
public class YamlResourceResolver extends AbstractResourceResolver{
	static{
		//classloader判断此类是否可用
		YamlProperties.class.getName();
	}
	
	public YamlResourceResolver(ConversionService conversionService) {
		super(conversionService);
	}

	public boolean matches(Resource resource, TypeDescriptor targetType) {
		return resource.exists() && resource.getFilename().endsWith(".yaml");
	}

	@Override
	protected Object resolve(Resource resource) throws IOException {
		YamlProperties yamlProperties = new YamlProperties(resource);
		return yamlProperties.get();
	}
}
