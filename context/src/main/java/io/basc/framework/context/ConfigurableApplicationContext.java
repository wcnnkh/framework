package io.basc.framework.context;

import java.io.Closeable;
import java.nio.charset.Charset;
import java.util.Properties;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.BeanFactoryPostProcessor;
import io.basc.framework.env1.ConfigurableEnvironment;
import io.basc.framework.event.observe.Observable;
import io.basc.framework.io.Resource;
import io.basc.framework.io.resolver.ConfigurablePropertiesResolver;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassLoaderAccessor;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.registry.Registration;

public interface ConfigurableApplicationContext extends ApplicationContext, ClassLoaderAccessor, Lifecycle, Closeable {
	@Override
	default ClassLoader getClassLoader() {
		return ClassLoaderAccessor.super.getClassLoader();
	}

	void addBeanFactoryPostProcessor(BeanFactoryPostProcessor beanFactoryPostProcessor);

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

	/**
	 * Register a shutdown hook with the JVM runtime, closing this context on JVM
	 * shutdown unless it has already been closed at that time.
	 * <p>
	 * This method can be called multiple times. Only one shutdown hook (at max)
	 * will be registered for each context instance.
	 * 
	 * @see java.lang.Runtime#addShutdownHook
	 * @see #close()
	 */
	Registration registerShutdownHook();

	void refresh() throws BeansException, IllegalStateException;

	@Override
	void close();
}
