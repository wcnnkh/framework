package io.basc.framework.factory.support;

import io.basc.framework.factory.ServiceLoader;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.MultiIterable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ServiceLoaders<S> implements ServiceLoader<S> {
	private List<ServiceLoader<S>> serviceLoaders;

	@SafeVarargs
	public ServiceLoaders(ServiceLoader<S>... serviceLoaders) {
		this(Arrays.asList(serviceLoaders));
	}

	public ServiceLoaders(List<ServiceLoader<S>> serviceLoaders) {
		this.serviceLoaders = CollectionUtils.isEmpty(serviceLoaders) ? null : serviceLoaders;
	}

	public void reload() {
		if (serviceLoaders == null) {
			return;
		}

		for (ServiceLoader<S> serviceLoader : serviceLoaders) {
			if (serviceLoader == null) {
				continue;
			}

			serviceLoader.reload();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Iterator<S> iterator() {
		return new MultiIterable(serviceLoaders).iterator();
	}
}
