package io.basc.framework.context.primary.resource;

import io.basc.framework.context.config.ApplicationContextSourceLoader;
import io.basc.framework.context.config.ConfigurableApplicationContext;
import io.basc.framework.io.Resource;
import io.basc.framework.util.Elements;

public interface ApplicationContextPrimaryResourceLoader extends ApplicationContextSourceLoader<Class<?>, Resource> {
	@Override
	Elements<Resource> load(ConfigurableApplicationContext context, Class<?> source);
}
