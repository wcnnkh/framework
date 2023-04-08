package io.basc.framework.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.support.StandardBroadcastEventDispatcher;

public class ServiceLoaders<S> extends StandardBroadcastEventDispatcher<ChangeEvent<ServiceLoader<S>>>
		implements ServiceLoader<S> {
	private Elements<ServiceLoader<S>> serviceLoaders;

	@SafeVarargs
	public ServiceLoaders(ServiceLoader<S>... serviceLoaders) {
		this(new ElementList<>(Arrays.asList(serviceLoaders)));
	}

	public ServiceLoaders(Elements<ServiceLoader<S>> serviceLoaders) {
		this.serviceLoaders = serviceLoaders;
	}

	public void reload() {
		if (serviceLoaders == null) {
			return;
		}

		ConsumeProcessor.consumeAll(serviceLoaders, (e) -> {
			if (e == null) {
				return;
			}

			e.reload();
		});
	}

	@Override
	public Iterator<S> iterator() {
		return CollectionUtils.iterator(serviceLoaders.iterator(), (e) -> e.iterator());
	}

	@Override
	public Stream<S> stream() {
		return serviceLoaders.filter((e) -> e != null).stream().flatMap((e) -> e.stream());
	}
}
