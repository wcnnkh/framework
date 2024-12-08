package io.basc.framework.context.primary.resource;

import io.basc.framework.context.config.ConfigurableApplicationContextSourceLoader;
import io.basc.framework.util.io.Resource;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigurableApplicationContextPrimaryResourceLoader extends
		ConfigurableApplicationContextSourceLoader<Class<?>, Resource, ApplicationContextPrimaryResourceLoader, ApplicationContextPrimaryResourceLoadExtender> {

	public ConfigurableApplicationContextPrimaryResourceLoader() {
		setServiceClass(ApplicationContextPrimaryResourceLoader.class);
		getExtender().setServiceClass(ApplicationContextPrimaryResourceLoadExtender.class);
	}
}
