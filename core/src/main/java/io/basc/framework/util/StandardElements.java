package io.basc.framework.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class StandardElements<E> implements Elements<E> {
	private final Iterable<E> iterable;

	public StandardElements(Iterable<E> iterable) {
		this.iterable = iterable;
	}

	@Override
	public Iterator<E> iterator() {
		if (iterable == null) {
			return Collections.emptyIterator();
		}

		return iterable.iterator();
	}

	@Override
	public Spliterator<E> spliterator() {
		if (iterable == null) {
			return Spliterators.emptySpliterator();
		}

		return iterable.spliterator();
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		Assert.requiredArgument(action != null, "action");
		if (iterable == null) {
			return;
		}
		iterable.forEach(action);
	}

	@Override
	public List<E> toList() {
		if (iterable instanceof List) {
			return Collections.unmodifiableList((List<E>) iterable);
		}
		return Elements.super.toList();
	}

	@Override
	public Set<E> toSet() {
		if (iterable instanceof Set) {
			return Collections.unmodifiableSet((Set<E>) iterable);
		}
		return Elements.super.toSet();
	}

	@Override
	public String toString() {
		return iterable.toString();
	}

	@Override
	public Stream<E> stream() {
		return Streams.stream(spliterator());
	}
}
