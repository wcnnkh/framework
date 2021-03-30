package scw.instance.support;

import scw.instance.NoArgsInstanceFactory;
import scw.instance.ServiceLoader;
import scw.instance.ServiceLoaderFactory;

public abstract class AbstractServiceLoaderFactory implements ServiceLoaderFactory {
	private final NoArgsInstanceFactory instanceFactory;

	public AbstractServiceLoaderFactory(NoArgsInstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	public NoArgsInstanceFactory getInstanceFactory() {
		return instanceFactory;
	}

	public ClassLoader getClassLoader() {
		return instanceFactory.getClassLoader();
	}
	
	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass, String ...defaultNames) {
		ServiceLoader<S> staticServiceLoader = new StaticServiceLoader<S>(instanceFactory, defaultNames);
		return new ServiceLoaders<S>(getServiceLoader(serviceClass), staticServiceLoader);
	}
}
