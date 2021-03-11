package scw.context.support;

import scw.context.ClassesLoader;
import scw.context.ContextLoader;
import scw.context.annotation.ProviderClassAccept;
import scw.context.annotation.ProviderServiceLoader;
import scw.env.Environment;
import scw.instance.InstanceUtils;
import scw.instance.NoArgsInstanceFactory;
import scw.instance.ServiceLoader;
import scw.instance.support.ServiceLoaders;
import scw.util.CollectionFactory;
import scw.util.GenericMap;
import scw.util.Supplier;

public class DefaultContextLoader extends
		DefaultContextClassesLoaderFactory implements ContextLoader {
	private final GenericMap<Class<?>, ServiceLoader<?>> serviceLoaderCacheMap = CollectionFactory
			.createHashMap(true);
	private final NoArgsInstanceFactory instanceFactory;
	protected volatile ClassesLoader<?> serviceClassesLoader;
	private final Environment environment;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DefaultContextLoader(Environment environment,
			NoArgsInstanceFactory instanceFactory) {
		super(true);
		this.environment = environment;
		setClassLoaderProvider(instanceFactory);
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
			synchronized (serviceLoaderCacheMap) {
				serviceLoader = serviceLoaderCacheMap.get(serviceClass);
				if(serviceLoader == null){
					ServiceLoader<S> parentServiceLoader = new ProviderServiceLoader<S>(getServiceClassesLoader(), instanceFactory, serviceClass);
					ServiceLoader<S> defaultServiceLoader = InstanceUtils.getServiceLoader(serviceClass, instanceFactory, environment);
					serviceLoader = new ServiceLoaders<S>(parentServiceLoader, defaultServiceLoader);
					serviceLoaderCacheMap.put(serviceClass, serviceLoader);
				}
			}
		}
		return (ServiceLoader<S>) serviceLoader;
	}
}
