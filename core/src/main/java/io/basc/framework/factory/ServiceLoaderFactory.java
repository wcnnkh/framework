package io.basc.framework.factory;

import java.util.Arrays;
import java.util.List;

import io.basc.framework.factory.support.NamedServiceLoader;
import io.basc.framework.util.ClassToString;
import io.basc.framework.util.ConvertibleIterable;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.ServiceLoaders;

public interface ServiceLoaderFactory extends InstanceFactory {
	<S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass);

	default <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass, String... defaultNames) {
		ServiceLoaders<S> serviceLoaderRegistry = new ServiceLoaders<>();
		serviceLoaderRegistry.register(new NamedServiceLoader<>(this, defaultNames));
		serviceLoaderRegistry.register(getServiceLoader(serviceClass));
		return serviceLoaderRegistry;
	}

	default <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass, Class<?>... defaultClasses) {
		ServiceLoaders<S> serviceLoaderRegistry = new ServiceLoaders<>();
		List<Class<?>> classes = Arrays.asList(defaultClasses);
		ConvertibleIterable<Class<?>, String> nameIterator = new ConvertibleIterable<Class<?>, String>(classes,
				ClassToString.NAME);
		ServiceLoader<S> staticServiceLoader = new NamedServiceLoader<S>(this, nameIterator);
		serviceLoaderRegistry.register(staticServiceLoader);
		serviceLoaderRegistry.register(getServiceLoader(serviceClass));
		return serviceLoaderRegistry;
	}
}
