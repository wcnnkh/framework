package io.basc.framework.util.element;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Streams;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class IterableElements<E> implements Elements<E>, Serializable {
	private static final long serialVersionUID = 1L;
	private volatile Iterable<E> iterable;

	public IterableElements(Iterable<E> iterable) {
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
	public ElementList<E> toList() {
		if (iterable instanceof List) {
			return new ElementList<>((List<E>) iterable);
		}
		return Elements.super.toList();
	}

	@Override
	public ElementSet<E> toSet() {
		if (iterable instanceof Set) {
			return new ElementSet<>((Set<E>) iterable);
		}
		return Elements.super.toSet();
	}

	@Override
	public Elements<E> reverse() {
		if (iterable instanceof List) {
			return Elements.of(() -> CollectionUtils.getIterator((List<E>) iterable, true));
		}
		return Elements.super.reverse();
	}

	@Override
	public long count() {
		if (iterable instanceof Collection) {
			return ((Collection<E>) iterable).size();
		}
		return Elements.super.count();
	}

	@Override
	public boolean isEmpty() {
		if (iterable instanceof Collection) {
			return ((Collection<E>) iterable).isEmpty();
		}
		return Elements.super.isEmpty();
	}

	@Override
	public Stream<E> stream() {
		return Streams.stream(spliterator());
	}

	@Override
	public Elements<E> cacheable() {
		if (iterable instanceof Collection) {
			return this;
		}
		return Elements.super.cacheable();
	}

	private void writeObject(ObjectOutputStream output) throws IOException {
		if (iterable instanceof Serializable) {
			output.defaultWriteObject();
		} else {
			this.iterable = toList();
			output.writeObject(iterable);
		}
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
		if (iterable instanceof Serializable) {
			input.defaultReadObject();
		} else {
			this.iterable = (Iterable<E>) input.readObject();
		}
	}
}
