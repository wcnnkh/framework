package io.basc.framework.factory.support;

import java.util.Arrays;

import io.basc.framework.factory.InstanceFactory;
import io.basc.framework.factory.ServiceLoader;
import io.basc.framework.util.Cursor;

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

	public Cursor<S> iterator() {
		return Cursor.of(new InstanceIterator<S>(instanceFactory, names.iterator()));
	}

}
