package io.basc.framework.beans.factory.ioc;

import java.lang.reflect.Method;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.beans.factory.spi.SPI;

public class ConfigurableIocResolver extends ConfigurableServices<IocResolver> implements IocResolver {
	private static volatile ConfigurableIocResolver defaults;

	public static ConfigurableIocResolver defaults() {
		if (defaults == null) {
			synchronized (ConfigurableIocResolver.class) {
				if (defaults == null) {
					defaults = new ConfigurableIocResolver();
					defaults.configure(SPI.global());
				}
			}
		}
		return defaults;
	}

	public ConfigurableIocResolver() {
		setServiceClass(IocResolver.class);
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
