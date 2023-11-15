package io.basc.framework.context;

import java.io.Closeable;

import io.basc.framework.beans.BeansException;
import io.basc.framework.context.config.ConfigurableClassScanner;
import io.basc.framework.env1.ConfigurableEnvironment;
import io.basc.framework.execution.aop.Aop;
import io.basc.framework.util.ClassLoaderAccessor;
import io.basc.framework.util.registry.Registration;

public interface ConfigurableApplicationContext extends ApplicationContext, ClassLoaderAccessor, Lifecycle, Closeable {
	@Override
	ConfigurableEnvironment getEnvironment();

	Aop getAop();

	Registration componentScan(String packageName);

	Registration source(Class<?> sourceClass);

	@Override
	ConfigurableClassScanner getClassScanner();

	@Override
	default ClassLoader getClassLoader() {
		return ClassLoaderAccessor.super.getClassLoader();
	}

	/**
	 * Load or refresh the persistent representation of the configuration, which
	 * might be from Java-based configuration, an XML file, a properties file, a
	 * relational database schema, or some other format.
	 * <p>
	 * As this is a startup method, it should destroy already created singletons if
	 * it fails, to avoid dangling resources. In other words, after invocation of
	 * this method, either all or no singletons at all should be instantiated.
	 * 
	 * @throws BeansException        if the bean factory could not be initialized
	 * @throws IllegalStateException if already initialized and multiple refresh
	 *                               attempts are not supported
	 */
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
	 * Close this application context, releasing all resources and locks that the
	 * implementation might hold. This includes destroying all cached singleton
	 * beans.
	 * <p>
	 * Note: Does <i>not</i> invoke {@code close} on a parent context; parent
	 * contexts have their own, independent lifecycle.
	 * <p>
	 * This method can be called multiple times without side effects: Subsequent
	 * {@code close} calls on an already closed context will be ignored.
	 */
	@Override
	void close();

	/**
	 * Determine whether this application context is active, that is, whether it has
	 * been refreshed at least once and has not been closed yet.
	 * 
	 * @return whether the context is still active
	 * @see #refresh()
	 * @see #close()
	 * @see #getBeanFactory()
	 */
	boolean isActive();
}
