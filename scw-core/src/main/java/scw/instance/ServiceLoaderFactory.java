package scw.instance;

import java.util.Arrays;
import java.util.List;

import scw.convert.ConvertibleIterable;
import scw.convert.lang.ClassToStringConverter;
import scw.instance.support.ServiceLoaders;
import scw.instance.support.StaticServiceLoader;

public interface ServiceLoaderFactory extends NoArgsInstanceFactory {
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
