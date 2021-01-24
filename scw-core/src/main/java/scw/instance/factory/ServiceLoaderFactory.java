package scw.instance.factory;

import scw.instance.ServiceLoader;

public interface ServiceLoaderFactory {
	<S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass);

	ClassLoader getClassLoader();
}
