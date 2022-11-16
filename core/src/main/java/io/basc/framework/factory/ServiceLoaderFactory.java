package io.basc.framework.factory;

import java.util.Arrays;
import java.util.List;

import io.basc.framework.convert.lang.ClassToStringConverter;
import io.basc.framework.factory.support.ServiceLoaders;
import io.basc.framework.factory.support.StaticServiceLoader;
import io.basc.framework.util.ConvertibleIterable;

public interface ServiceLoaderFactory extends InstanceFactory {
	/**
	 * 获取一个类实现的加载器
	 * 
	 * @param              <S>
	 * @param serviceClass
	 * @return
	 */
	<S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass);

	default <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass, String... defaultNames) {
		ServiceLoader<S> staticServiceLoader = new StaticServiceLoader<S>(this, defaultNames);
		return new ServiceLoaders<S>(getServiceLoader(serviceClass), staticServiceLoader);
	}

	default <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass, Class<?>... defaultClasses) {
		List<Class<?>> classes = Arrays.asList(defaultClasses);
		ConvertibleIterable<Class<?>, String> nameIterator = new ConvertibleIterable<Class<?>, String>(classes,
				ClassToStringConverter.NAME);
		ServiceLoader<S> staticServiceLoader = new StaticServiceLoader<S>(this, nameIterator);
		return new ServiceLoaders<S>(getServiceLoader(serviceClass), staticServiceLoader);
	}
}
