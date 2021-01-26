package scw.instance;

import scw.util.ClassLoaderProvider;


public interface ServiceLoaderFactory extends ClassLoaderProvider {
	<S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass);
}
