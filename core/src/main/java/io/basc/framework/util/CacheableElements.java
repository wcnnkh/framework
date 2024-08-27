package io.basc.framework.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Stream;

import lombok.NonNull;

public class CacheableElements<E, C extends Collection<E>>
		implements ServiceLoader<E>, CollectionElementsWrapper<E, C>, Serializable {
	private static final long serialVersionUID = 1L;
	@NonNull
	private final transient Streamable<E> streamable;
	@NonNull
	private final transient Collector<? super E, ?, C> collector;
	private volatile C cached;

	public CacheableElements(@NonNull Streamable<E> streamable, @NonNull Collector<? super E, ?, C> collector) {
		this.streamable = streamable;
		this.collector = collector;
	}

	@Override
	public ServiceLoader<E> cacheable() {
		return this;
	}

	@Override
	public C getSource() {
		if (cached == null) {
			reload(false);
		}
		return cached;
	}

	@Override
	public void reload() {
		reload(true);
	}

	public void reload(boolean force) {
		if (collector == null || streamable == null) {
			return;
		}

		synchronized (this) {
			if (cached == null || force) {
				cached = streamable.collect(collector);
			}
		}
	}

	@Override
	public int hashCode() {
		return getSource().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof CacheableElements) {
			CacheableElements<?, ?> other = (CacheableElements<?, ?>) obj;
			return ObjectUtils.equals(getSource(), other.getSource());
		}
		return getSource().equals(obj);
	}

	@Override
	public String toString() {
		return getSource().toString();
	}

	private void writeObject(ObjectOutputStream output) throws IOException {
		output.writeObject(getSource());
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
		this.cached = (C) input.readObject();
	}

	@Override
	public Stream<E> stream() {
		return getSource().stream();
	}
}
