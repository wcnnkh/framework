package io.basc.framework.context.resource;

import java.io.IOException;
import java.util.Properties;

import io.basc.framework.context.config.ConfigurableApplicationContext;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.collection.MapProperties;
import io.basc.framework.core.env.PropertySource;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.io.Resource;
import io.basc.framework.util.io.resolver.ConfigurablePropertiesResolver;
import io.basc.framework.util.logging.LogManager;
import io.basc.framework.util.logging.Logger;

public class ApplicationContextPropertiesResourceImporter extends ConfigurablePropertiesResolver
		implements ApplicationContextResourceImporter {
	private static Logger logger = LogManager.getLogger(ApplicationContextPropertiesResourceImporter.class);

	@Override
	public void process(ConfigurableApplicationContext context, Elements<? extends Resource> source) {
		for (Resource resource : source) {
			Properties properties = new Properties();
			try {
				resolveProperties(properties, resource);
			} catch (IOException e) {
				logger.error(e, resource.toString());
				continue;
			}
			MapProperties mapProperties = new MapProperties(properties, TypeDescriptor.valueOf(Properties.class),
					context.getEnvironment().getConversionService());
			PropertySource propertySource = PropertySource.forProperties(resource.getName(), mapProperties);
			context.getEnvironment().getPropertySources().register(propertySource);
		}
	}
}
