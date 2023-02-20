package io.basc.framework.factory.support;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.basc.framework.factory.ServiceLoader;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Cursor;
import io.basc.framework.util.Cursors;

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

	public Cursor<S> iterator() {
		Iterator<Cursor<S>> iterator = serviceLoaders.stream().filter((e) -> e != null).map((e) -> e.iterator())
				.iterator();
		return new Cursors<>(iterator);
	}
}
