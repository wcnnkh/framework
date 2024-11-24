package io.basc.framework.context.config;

import java.io.Closeable;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.BeanFactoryPostProcessor;
import io.basc.framework.beans.factory.config.ConfigurableBeanFactory;
import io.basc.framework.beans.factory.config.ConfigurableListableBeanFactory;
import io.basc.framework.context.ApplicationContext;
import io.basc.framework.context.ApplicationContextEvent;
import io.basc.framework.core.env.ConfigurableEnvironment;
import io.basc.framework.io.ProtocolResolver;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassLoaderAccessor;
import io.basc.framework.util.Lifecycle;
import io.basc.framework.util.actor.batch.BatchEventDispatcher;
import io.basc.framework.util.register.Registration;

public interface ConfigurableApplicationContext extends ApplicationContext, ClassLoaderAccessor, Lifecycle, Closeable,
		ConfigurableBeanFactory, BatchEventDispatcher<ApplicationContextEvent> {
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

	boolean isActive();

	void refresh() throws BeansException, IllegalStateException;

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

	ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
}
