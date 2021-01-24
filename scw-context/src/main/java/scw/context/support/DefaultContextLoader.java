package scw.context.support;

import scw.context.ContextLoader;
import scw.env.Environment;
import scw.instance.ServiceLoader;
import scw.instance.factory.NoArgsInstanceFactory;
import scw.util.CollectionFactory;
import scw.util.GenericMap;

public class DefaultContextLoader extends
		DefaultContextClassesLoaderFactory implements ContextLoader {

	private final GenericMap<Class<?>, ServiceLoader<?>> serviceLoaderCacheMap = CollectionFactory
			.createHashMap(true);
	private final NoArgsInstanceFactory instanceFactory;
	private final Environment environment;

	public DefaultContextLoader(Environment environment,
			NoArgsInstanceFactory instanceFactory) {
		super(true);
		this.environment = environment;
		this.instanceFactory = instanceFactory;
	}

	@SuppressWarnings("unchecked")
	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		ServiceLoader<?> serviceLoader = serviceLoaderCacheMap
				.get(serviceClass);
		if (serviceLoader == null) {
			serviceLoader = new ContextServiceLoader<S>(serviceClass,
					instanceFactory, environment, getContextClassesLoader());
			ServiceLoader<?> cache = serviceLoaderCacheMap.putIfAbsent(
					serviceClass, serviceLoader);
			if (cache != null) {
				serviceLoader = cache;
			}
		}
		return (ServiceLoader<S>) serviceLoader;
	}

	public ClassLoader getClassLoader() {
		return instanceFactory.getClassLoader();
	}
}
