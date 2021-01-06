package scw.configure.resolver;

import scw.convert.ConversionService;
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
		super(conversionService, "*.yaml");
	}
	
	@Override
	protected Object resolve(Resource resource) {
		YamlProperties yamlProperties = new YamlProperties(resource);
		return yamlProperties.get();
	}
}
