package io.basc.framework.context.config;

import java.io.Closeable;
import java.nio.charset.Charset;
import java.util.Properties;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.BeanFactoryPostProcessor;
import io.basc.framework.beans.factory.config.ConfigurableBeanFactory;
import io.basc.framework.context.ApplicationContext;
import io.basc.framework.context.Lifecycle;
import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.io.ProtocolResolver;
import io.basc.framework.io.Resource;
import io.basc.framework.io.resolver.ConfigurablePropertiesResolver;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassLoaderAccessor;
import io.basc.framework.util.Registration;
import io.basc.framework.util.element.Elements;
import io.basc.framework.value.observe.Observable;

public interface ConfigurableApplicationContext
		extends ApplicationContext, ClassLoaderAccessor, Lifecycle, Closeable, ConfigurableBeanFactory {
	void addBeanFactoryPostProcessor(BeanFactoryPostProcessor beanFactoryPostProcessor);

	void addProtocolResolver(ProtocolResolver protocolResolver);

	@Override
	void close();

	@Override
	default ClassLoader getClassLoader() {
		return ClassLoaderAccessor.super.getClassLoader();
	}

	@Override
	ConfigurableEnvironment getEnvironment();

	@Override
	ConfigurablePropertiesResolver getPropertiesResolver();

	boolean isActive();

	void refresh() throws BeansException, IllegalStateException;

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

	/**
	 * Set the {@code Environment} for this application context.
	 * 
	 * @param environment the new environment
	 */
	void setEnvironment(ConfigurableEnvironment environment);

	/**
	 * Set the parent of this application context.
	 * <p>
	 * Note that the parent shouldn't be changed: It should only be set outside a
	 * constructor if it isn't available when an object of this class is created,
	 * for example in case of WebApplicationContext setup.
	 * 
	 * @param parent the parent context
	 */
	void setParent(@Nullable ApplicationContext parent);
}
