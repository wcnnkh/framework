package io.basc.framework.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;

public class CachedElements<E> implements Elements<E>, Serializable {
	private static final long serialVersionUID = 1L;
	private final transient Elements<E> elements;
	private volatile List<E> list;

	public CachedElements(Elements<E> elements) {
		this.elements = elements;
	}

	@Override
	public long count() {
		return toList().count();
	}

	@Override
	public <U> Elements<U> convert(Function<? super Stream<E>, ? extends Stream<U>> converter) {
		if (list != null) {
			return Elements.of(list).convert(converter);
		}
		return elements.convert(converter);
	}

	@Override
	public Iterator<E> iterator() {
		if (list != null) {
			return list.iterator();
		}
		return elements.iterator();
	}

	@Override
	public Spliterator<E> spliterator() {
		if (list != null) {
			return list.spliterator();
		}
		return elements.spliterator();
	}

	@Override
	public Stream<E> stream() {
		if (list != null) {
			return list.stream();
		}
		return elements.stream();
	}

	@Override
	public boolean isEmpty() {
		if (list != null) {
			return list.isEmpty();
		}
		return elements.isEmpty();
	}

	@Override
	public ElementList<E> toList() {
		if (list == null) {
			synchronized (this) {
				if (list == null) {
					list = Elements.super.toList();
				}
			}
		}
		return new ElementList<>(list);
	}
	
	@Override
	public Elements<E> cacheable() {
		return this;
	}

	@Override
	public ElementSet<E> toSet() {
		return toList().toSet();
	}

	private void writeObject(ObjectOutputStream output) throws IOException {
		toList();
		output.writeObject(list);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
		this.list = (List<E>) input.readObject();
	}
}
