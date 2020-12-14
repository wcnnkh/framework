package scw.yaml;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import scw.io.Resource;
import scw.io.event.AbstractObservableResources;
import scw.yaml.YamlProcessor.MatchCallback;

public class ObservableYamlProperties extends
		AbstractObservableResources<Properties> {
	private YamlProcessor processor;
	private Collection<Resource> resources;
	
	public ObservableYamlProperties(Collection<Resource> resources) {
		this(new YamlProcessor(), resources);
	}

	public ObservableYamlProperties(YamlProcessor processor,
			Collection<Resource> resources) {
		this.processor = processor;
		this.resources = resources;
	}

	@Override
	public Collection<Resource> getResources() {
		return resources;
	}

	public Properties forceGet() {
		final Properties properties = new Properties();
		processor.process(new MatchCallback() {

			public void process(Properties properties, Map<String, Object> map) {
				properties.putAll(properties);
			}
		}, resources);
		return properties;
	}
}
