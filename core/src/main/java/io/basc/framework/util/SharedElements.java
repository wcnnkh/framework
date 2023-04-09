package io.basc.framework.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class SharedElements<E> implements Elements<E> {
	private final Iterable<E> iterable;

	public SharedElements(Iterable<E> iterable) {
		Assert.requiredArgument(iterable != null, "iterable");
		this.iterable = iterable;
	}

	@Override
	public Iterator<E> iterator() {
		return iterable.iterator();
	}

	@Override
	public Spliterator<E> spliterator() {
		return iterable.spliterator();
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		Assert.requiredArgument(action != null, "action");
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
	public Stream<E> stream() {
		return Streams.stream(spliterator());
	}
}
