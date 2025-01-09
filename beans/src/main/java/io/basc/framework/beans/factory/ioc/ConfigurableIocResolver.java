package io.basc.framework.beans.factory.ioc;

import java.lang.reflect.Method;

import io.basc.framework.util.spi.ConfigurableServices;

public class ConfigurableIocResolver extends ConfigurableServices<IocResolver> implements IocResolver {
	private static volatile ConfigurableIocResolver defaults;

	public static ConfigurableIocResolver defaults() {
		if (defaults == null) {
			synchronized (ConfigurableIocResolver.class) {
				if (defaults == null) {
					defaults = new ConfigurableIocResolver();
					defaults.doNativeConfigure();
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
		return anyMatch((e) -> e.isInitMethod(method));
	}

	@Override
	public boolean isDestroyMethod(Method method) {
		return anyMatch((e) -> e.isDestroyMethod(method));
	}

}
