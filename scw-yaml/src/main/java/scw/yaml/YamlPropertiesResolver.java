package scw.yaml;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;

import scw.io.Resource;
import scw.io.resolver.PropertiesResolver;
import scw.yaml.YamlProcessor.MatchCallback;

public class YamlPropertiesResolver implements PropertiesResolver{
	private final YamlProcessor processor;
	
	public YamlPropertiesResolver(){
		this(new YamlProcessor());
	}
	
	public YamlPropertiesResolver(YamlProcessor processor){
		this.processor = processor;
	}
	
	public boolean isSupportResolveProperties(Resource resource) {
		return resource.exists() && resource.getFilename().endsWith(".yaml");
	}

	public void resolveProperties(final Properties load, Resource resource,
			Charset charset) {
		processor.process(new MatchCallback() {

			public void process(Properties properties, Map<String, Object> map) {
				load.putAll(properties);
			}
		}, charset, resource);
	}

}
