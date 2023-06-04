package io.basc.framework.beans.factory;

import io.basc.framework.beans.factory.support.ClassesServiceLoader;
import io.basc.framework.beans.factory.support.NamesServiceLoader;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ServiceLoader;

public interface ServiceLoaderFactory extends BeanFactory {
	<S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass);

	default <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass, String... defaultNames) {
		NamesServiceLoader<S> namesServiceLoader = new NamesServiceLoader<>(this, serviceClass,
				Elements.forArray(defaultNames));
		ServiceLoader<S> serviceLoader = getServiceLoader(serviceClass);
		return namesServiceLoader.concat(serviceLoader);
	}

	@SuppressWarnings("unchecked")
	default <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass, Class<? extends S>... defaultClasses) {
		ClassesServiceLoader<S> classesServiceLoader = new ClassesServiceLoader<>(this,
				Elements.forArray(defaultClasses));
		ServiceLoader<S> serviceLoader = getServiceLoader(serviceClass);
		return classesServiceLoader.concat(serviceLoader);
	}
}
