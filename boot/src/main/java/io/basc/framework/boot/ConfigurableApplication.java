package io.basc.framework.boot;

import java.nio.charset.Charset;
import java.util.Properties;

import io.basc.framework.context.config.ConfigurableApplicationContext;
import io.basc.framework.env1.ConfigurableEnvironment;
import io.basc.framework.event.observe.Observable;
import io.basc.framework.io.Resource;
import io.basc.framework.io.resolver.ConfigurablePropertiesResolver;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.registry.Registration;

public interface ConfigurableApplication extends Application, ConfigurableApplicationContext {

	@Override
	ConfigurableEnvironment getEnvironment();

	@Override
	ConfigurablePropertiesResolver getPropertiesResolver();

	default Registration registerProfileResources(Elements<? extends Resource> profileResources,
			@Nullable Charset charset) {
		Observable<Properties> observable = toObservableProperties(profileResources, getPropertiesResolver(), charset);
		return getEnvironment().registerProperties(observable);
	}

	default Registration registerProfileResources(String location, @Nullable Charset charset) {
		Elements<Resource> resources = getProfileResources(location);
		return registerProfileResources(resources, charset);
	}
}
