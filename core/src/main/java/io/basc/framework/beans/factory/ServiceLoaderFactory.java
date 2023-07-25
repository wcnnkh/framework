package io.basc.framework.beans.factory;

import java.util.Optional;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.InstanceFactory;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.spi.CachedServiceLoader;
import io.basc.framework.util.spi.ServiceLoader;

public interface ServiceLoaderFactory extends BeanFactory, InstanceFactory {

	<S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass);

	@SuppressWarnings("unchecked")
	default <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass, @Nullable Class<?>... defaultClasses) {
		ServiceLoader<S> serviceLoader = getServiceLoader(serviceClass);
		if (defaultClasses != null) {
			Elements<S> defaultElements = Elements.forArray(defaultClasses)
					.filter((e) -> e != null && canInstantiated(e)).map((e) -> newInstance(e)).map((e) -> (S) e);
			CachedServiceLoader<S> defaultServiceLoader = new CachedServiceLoader<>(defaultElements);
			return serviceLoader.concat(defaultServiceLoader);
		}
		return serviceLoader;
	}

	/**
	 * 加载一个服务
	 * 
	 * @param <S>
	 * @param serviceClass
	 * @return
	 */
	default <S> Optional<S> loadService(Class<S> serviceClass) {
		// 默认使用第一个服务
		return getServiceLoader(serviceClass).getServices().findFirst();
	}
}
