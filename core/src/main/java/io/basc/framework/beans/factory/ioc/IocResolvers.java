package io.basc.framework.beans.factory.ioc;

import java.lang.reflect.Method;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.beans.factory.spi.SPI;

public class IocResolvers extends ConfigurableServices<IocResolver> implements IocResolver {
	private static volatile IocResolvers defaults;

	public static IocResolvers defaults() {
		if (defaults == null) {
			synchronized (IocResolvers.class) {
				if (defaults == null) {
					defaults = new IocResolvers();
					defaults.configure(SPI.global());
				}
			}
		}
		return defaults;
	}

	public IocResolvers() {
		super(IocResolver.class);
	}

	@Override
	public boolean isInitMethod(Method method) {
		return getServices().anyMatch((e) -> e.isInitMethod(method));
	}

	@Override
	public boolean isDestroyMethod(Method method) {
		return getServices().anyMatch((e) -> e.isDestroyMethod(method));
	}

}
