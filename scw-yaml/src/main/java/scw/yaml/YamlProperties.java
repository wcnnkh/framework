package scw.yaml;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import scw.io.Resource;
import scw.io.event.AbstractObservableResources;
import scw.yaml.YamlProcessor.MatchCallback;

public class YamlProperties extends
		AbstractObservableResources<Properties> {
	private YamlProcessor processor;
	private Collection<Resource> resources;
	
	public YamlProperties(Resource ...resources) {
		this(Arrays.asList(resources));
	}
	
	public YamlProperties(YamlProcessor processor, Resource ...resources) {
		this(processor, Arrays.asList(resources));
	}
	
	public YamlProperties(Collection<Resource> resources) {
		this(new YamlProcessor(), resources);
	}

	public YamlProperties(YamlProcessor processor,
			Collection<Resource> resources) {
		this.processor = processor;
		this.resources = resources;
	}

	@Override
	public Collection<Resource> getResources() {
		return resources;
	}

	public Properties forceGet() {
		final Properties allProperties = new Properties();
		processor.process(new MatchCallback() {

			public void process(Properties properties, Map<String, Object> map) {
				allProperties.putAll(properties);
			}
		}, null, resources);
		return allProperties;
	}
}
