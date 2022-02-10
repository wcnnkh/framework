package io.basc.framework.factory;

import io.basc.framework.factory.support.ConfigServiceLoader;
import io.basc.framework.factory.support.ServiceLoaders;
import io.basc.framework.factory.support.SpiServiceLoader;
import io.basc.framework.lang.Constants;
import io.basc.framework.util.AntPathMatcher;
import io.basc.framework.util.StringMatchers;
import io.basc.framework.value.ValueFactory;

public abstract class AbstractServiceLoaderFactory extends AbstractNoArgsInstanceFactoryWrapper
		implements ServiceLoaderFactory {
	private static final String ENABLE_PREFIX = "io.basc.framework.spi";
	private final AntPathMatcher antPathMatcher = new AntPathMatcher(".");
	/**
	 * 是否强制使用spi
	 */
	private boolean forceSpi = false;

	public boolean isForceSpi() {
		return forceSpi;
	}

	public void setForceSpi(boolean forceSpi) {
		this.forceSpi = forceSpi;
	}

	protected abstract ValueFactory<String> getConfigFactory();

	protected boolean useSpi(Class<?> serviceClass) {
		String[] prefixs = getConfigFactory().getObject(ENABLE_PREFIX, String[].class);
		if (prefixs == null) {
			return false;
		}

		for (String prefix : prefixs) {
			if (StringMatchers.matchAny(prefix, serviceClass.getName())
					|| antPathMatcher.match(prefix, serviceClass.getName())) {
				return true;
			}
		}
		return false;
	}

	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		ServiceLoader<S> configServiceLoader = new ConfigServiceLoader<S>(serviceClass, getConfigFactory(), this);
		if (isForceSpi() || serviceClass.getName().startsWith(Constants.SYSTEM_PACKAGE_NAME) || useSpi(serviceClass)) {
			ServiceLoader<S> spiServiceLoader = new SpiServiceLoader<S>(serviceClass, this);
			return new ServiceLoaders<S>(configServiceLoader, spiServiceLoader);
		}
		return configServiceLoader;
	}
}
