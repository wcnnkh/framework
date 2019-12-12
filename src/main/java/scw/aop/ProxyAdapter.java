package scw.aop;

import java.util.Collection;

import scw.core.instance.InstanceFactory;

//TODO 未完成
public interface ProxyAdapter {
	boolean isSupport(Class<?> clazz);

	<T> T proxy(Class<T> clazz, Collection<Class<?>> interfaceClass, Collection<Filter> filters);

	<T> T proxy(Class<? extends T> clazz, T bean, Collection<Class<?>> interfaceClass, Collection<Filter> filters);

	<T> T proxy(Class<? extends T> clazz, Collection<Class<?>> interfaceClass, InstanceFactory instanceFactory,
			Collection<String> filters);

	<T> T proxy(Class<? extends T> clazz, T bean, Collection<Class<?>> interfaceClass, InstanceFactory instanceFactory,
			Collection<String> filters);
}