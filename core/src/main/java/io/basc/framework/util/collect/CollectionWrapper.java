package io.basc.framework.util.collect;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import io.basc.framework.util.Wrapper;

public class CollectionWrapper<E, W extends Collection<E>> extends Wrapper<W> implements Collection<E>, Serializable {
	private static final long serialVersionUID = 1L;

	public CollectionWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public int size() {
		return wrappedTarget.size();
	}

	@Override
	public boolean isEmpty() {
		return wrappedTarget.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return wrappedTarget.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return wrappedTarget.iterator();
	}

	@Override
	public Object[] toArray() {
		return wrappedTarget.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return wrappedTarget.toArray(a);
	}

	@Override
	public boolean add(E e) {
		return wrappedTarget.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return wrappedTarget.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return wrappedTarget.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return wrappedTarget.addAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return wrappedTarget.retainAll(c);
	}

	@Override
	public void clear() {
		wrappedTarget.clear();
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		wrappedTarget.forEach(action);
	}

	@Override
	public Stream<E> parallelStream() {
		return wrappedTarget.parallelStream();
	}

	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		return wrappedTarget.removeIf(filter);
	}

	@Override
	public Spliterator<E> spliterator() {
		return wrappedTarget.spliterator();
	}

	@Override
	public Stream<E> stream() {
		return wrappedTarget.stream();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return wrappedTarget.removeAll(c);
	}
}
