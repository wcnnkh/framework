package io.basc.framework.factory.support;

import java.util.Arrays;
import java.util.Iterator;

import io.basc.framework.factory.InstanceFactory;
import io.basc.framework.util.ServiceLoader;

public final class NamedServiceLoader<S> implements ServiceLoader<S> {
	private final Iterable<String> names;
	private final InstanceFactory instanceFactory;

	public NamedServiceLoader(InstanceFactory instanceFactory, Iterable<String> names) {
		this.instanceFactory = instanceFactory;
		this.names = names;
	}

	public NamedServiceLoader(InstanceFactory instanceFactory, String... names) {
		this(instanceFactory, Arrays.asList(names));
	}

	public void reload() {
	}

	public Iterator<S> iterator() {
		return new InstanceIterator<S>(instanceFactory, names.iterator());
	}

}
