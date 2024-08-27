package io.basc.framework.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collector;

import lombok.NonNull;

public class CollectionElements<E, C extends Collection<E>> extends CacheableElements<E, C> {
	private static final long serialVersionUID = 1L;

	public CollectionElements(@NonNull Streamable<E> streamable, @NonNull Collector<? super E, ?, C> collector) {
		super(streamable, collector);
		reload(true);
	}

	@Override
	public int size() {
		return getSource().size();
	}

	@Override
	public Iterator<E> iterator() {
		return getSource().iterator();
	}
}
