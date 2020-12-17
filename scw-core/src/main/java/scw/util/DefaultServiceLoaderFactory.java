package scw.util;

import scw.core.instance.NoArgsInstanceFactory;
import scw.core.utils.ClassUtils;

public class DefaultServiceLoaderFactory implements ServiceLoaderFactory {
	private NoArgsInstanceFactory instanceFactory;

	public DefaultServiceLoaderFactory() {
	}

	public DefaultServiceLoaderFactory(NoArgsInstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
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
		return getServiceLoader(service, ClassUtils.getDefaultClassLoader());
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
