package io.basc.framework.instance;

import io.basc.framework.instance.support.ConfigServiceLoader;
import io.basc.framework.instance.support.ServiceLoaders;
import io.basc.framework.instance.support.SpiServiceLoader;
import io.basc.framework.lang.Constants;
import io.basc.framework.value.ValueFactory;

public abstract class AbstractServiceLoaderFactory extends AbstractNoArgsInstanceFactoryWrapper
		implements ServiceLoaderFactory {
	protected abstract ValueFactory<String> getConfigFactory();

	protected boolean useSpi(Class<?> serviceClass) {
		return serviceClass.getName().startsWith(Constants.SYSTEM_PACKAGE_NAME);
	}

	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		ServiceLoader<S> configServiceLoader = new ConfigServiceLoader<S>(serviceClass, getConfigFactory(), this);
		ServiceLoader<S> spiServiceLoader = null;
		if (useSpi(serviceClass)) {
			spiServiceLoader = new SpiServiceLoader<S>(serviceClass, this);
		}
		return new ServiceLoaders<S>(configServiceLoader, spiServiceLoader);
	}
}
