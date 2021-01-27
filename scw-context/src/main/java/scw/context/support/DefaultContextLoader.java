package scw.context.support;

import scw.context.ClassesLoader;
import scw.context.ContextLoader;
import scw.context.annotation.ProviderClassAccept;
import scw.env.Environment;
import scw.instance.NoArgsInstanceFactory;
import scw.instance.ServiceLoader;
import scw.util.CollectionFactory;
import scw.util.GenericMap;
import scw.util.Supplier;

public class DefaultContextLoader extends
		DefaultContextClassesLoaderFactory implements ContextLoader {
	private final GenericMap<Class<?>, ServiceLoader<?>> serviceLoaderCacheMap = CollectionFactory
			.createHashMap(true);
	private final NoArgsInstanceFactory instanceFactory;
	private final Environment environment;
	protected volatile ClassesLoader<?> serviceClassesLoader;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DefaultContextLoader(Environment environment,
			NoArgsInstanceFactory instanceFactory) {
		super(true);
		setClassLoaderProvider(instanceFactory);
		this.environment = environment;
		this.instanceFactory = instanceFactory;
		Supplier<String> packageName = environment.getObservableValue("context.package.name", String.class, null);
		ClassesLoader contextClassesLoader = new ClassScannerClassesLoader(this, this, packageName, this);
		getContextClassesLoader().add(contextClassesLoader);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ClassesLoader<?> getServiceClassesLoader(){
		if(serviceClassesLoader == null){
			synchronized (this) {
				if(serviceClassesLoader == null){
					serviceClassesLoader = new AcceptClassesLoader(getContextClassesLoader(), ProviderClassAccept.INSTANCE, true);
				}
			}
		}
		return serviceClassesLoader;
	}

	@SuppressWarnings("unchecked")
	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		ServiceLoader<?> serviceLoader = serviceLoaderCacheMap
				.get(serviceClass);
		if (serviceLoader == null) {
			serviceLoader = new ContextServiceLoader<S>(serviceClass,
					instanceFactory, environment, getServiceClassesLoader());
			ServiceLoader<?> cache = serviceLoaderCacheMap.putIfAbsent(
					serviceClass, serviceLoader);
			if (cache != null) {
				serviceLoader = cache;
			}
		}
		return (ServiceLoader<S>) serviceLoader;
	}
}
