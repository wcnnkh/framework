package io.basc.framework.dubbo;

import java.util.Collection;

import org.apache.dubbo.config.ServiceConfig;

public interface DubboServiceRegistry {
	Collection<ServiceConfig<?>> getServices();

	void register(ServiceConfig<?> serviceConfig);

	<T> ServiceConfig<T> register(Class<? extends T> serviceClass, T service);
}
