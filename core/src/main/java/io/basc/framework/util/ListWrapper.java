package io.basc.framework.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.UnaryOperator;

public class ListWrapper<E> extends CollectionWrapper<E, List<E>> implements List<E> {
	private static final long serialVersionUID = 1L;

	public ListWrapper(List<E> wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		return wrappedTarget.addAll(index, c);
	}

	@Override
	public E get(int index) {
		return wrappedTarget.get(index);
	}

	@Override
	public E set(int index, E element) {
		return wrappedTarget.set(index, element);
	}

	@Override
	public void add(int index, E element) {
		wrappedTarget.add(index, element);
	}

	@Override
	public E remove(int index) {
		return wrappedTarget.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return wrappedTarget.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return wrappedTarget.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return wrappedTarget.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return wrappedTarget.listIterator(index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return wrappedTarget.subList(fromIndex, toIndex);
	}

	@Override
	public void sort(Comparator<? super E> c) {
		wrappedTarget.sort(c);
	}

	@Override
	public void replaceAll(UnaryOperator<E> operator) {
		wrappedTarget.replaceAll(operator);
	}
}
