package io.basc.framework.context.resource;

import java.nio.charset.Charset;

import io.basc.framework.context.config.ConfigurableApplicationContext;
import io.basc.framework.lang.Nullable;
import io.basc.framework.observe.properties.DynamicPropertyRegistry;
import io.basc.framework.observe.properties.ObservablePropertyFactory;
import io.basc.framework.util.collection.Elements;
import io.basc.framework.util.io.Resource;
import io.basc.framework.util.io.resolver.ConfigurablePropertiesResolver;

public class ApplicationContextPropertiesResourceImporter extends ConfigurablePropertiesResolver
		implements ApplicationContextResourceImporter {

	@Override
	public void process(ConfigurableApplicationContext context, Elements<? extends Resource> source) {
		ObservablePropertyFactory observablePropertyFactory = toObservableProperties(source, null);
		context.getEnvironment().register(observablePropertyFactory);
	}

	public ObservablePropertyFactory toObservableProperties(Elements<? extends Resource> resources,
			@Nullable Charset charset) {
		DynamicPropertyRegistry registry = new DynamicPropertyRegistry();
		for (Resource resource : resources) {
			registry.registerResource(resource, this, charset);
		}
		return registry;
	}
}
