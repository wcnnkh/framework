package scw.instance.factory;

import scw.core.utils.ClassUtils;
import scw.instance.ServiceLoader;
import scw.instance.support.SpiServiceLoader;


public class DefaultServiceLoaderFactory implements ServiceLoaderFactory {
	private NoArgsInstanceFactory instanceFactory;
	private ClassLoader classLoader;

	public DefaultServiceLoaderFactory() {
	}

	public DefaultServiceLoaderFactory(NoArgsInstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	public ClassLoader getClassLoader() {
		return classLoader == null? ClassUtils.getDefaultClassLoader():classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public <S> ServiceLoader<S> getServiceLoader(Class<S> service,
			ClassLoader loader) {
		SpiServiceLoader<S> serviceLoader = new SpiServiceLoader<S>(service,
				loader);
		if (instanceFactory != null) {
			serviceLoader.setInstanceFactory(instanceFactory);
		}
		return serviceLoader;
	}

	public <S> ServiceLoader<S> getServiceLoader(Class<S> service) {
		return getServiceLoader(service, getClassLoader());
	}

	public <S> ServiceLoader<S> getServiceLoaderInstalled(Class<S> service) {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		ClassLoader prev = null;
		while (cl != null) {
			prev = cl;
			cl = cl.getParent();
		}
		return getServiceLoader(service, prev);
	}
}
