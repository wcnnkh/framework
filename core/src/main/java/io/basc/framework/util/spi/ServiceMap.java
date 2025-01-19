package io.basc.framework.util.spi;

import java.util.TreeMap;
import java.util.function.Function;

import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.comparator.TypeComparator;
import io.basc.framework.util.register.PayloadRegistration;
import io.basc.framework.util.register.container.MultiValueMapContainer;
import lombok.NonNull;

public class ServiceMap<S> extends
		MultiValueMapContainer<Class<?>, S, PayloadRegistration<S>, Services<S>, TreeMap<Class<?>, Services<S>>> {
	public ServiceMap() {
		this((key) -> new Services<>());
	}

	public ServiceMap(@NonNull Function<? super Class<?>, ? extends Services<S>> servicesCreator) {
		super(() -> new TreeMap<>(TypeComparator.DEFAULT), servicesCreator);
	}

	/**
	 * 搜索对应的服务列表
	 * 
	 * @param requiredType
	 * @return
	 */
	public Elements<S> search(Class<?> requiredType) {
		return readAsElements((map) -> {
			if (map == null) {
				return Elements.empty();
			}

			Services<S> services = map.get(requiredType);
			if (services != null) {
				return services;
			}

			return Elements.of(() -> map.entrySet().stream().filter((e) -> requiredType.isAssignableFrom(e.getKey()))
					.flatMap((e) -> e.getValue().stream()));
		});
	}
}
