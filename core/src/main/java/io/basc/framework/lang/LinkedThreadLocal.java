package io.basc.framework.lang;

import io.basc.framework.util.ObjectUtils;

import java.util.LinkedList;

public class LinkedThreadLocal<E> {
	private final ThreadLocal<LinkedList<E>> local;

	public LinkedThreadLocal(String name) {
		this.local = new NamedThreadLocal<LinkedList<E>>(name);
	}

	public E getCurrent() {
		LinkedList<E> list = local.get();
		return list == null ? null : list.getLast();
	}

	public void remove(E element) {
		LinkedList<E> list = local.get();
		if (list == null) {
			throw new IllegalStateException("remove nesting element " + element);
		}

		E nesting = list.getLast();
		if (!ObjectUtils.equals(element, nesting)) {
			throw new IllegalStateException("remove nesting [" + nesting + "] conversion service [" + element + "]");
		}

		list.removeLast();
		if (list.isEmpty()) {
			local.remove();
		} else {
			local.set(list);
		}
	}

	public void set(E element) {
		LinkedList<E> list = local.get();
		if (list == null) {
			list = new LinkedList<E>();
			local.set(list);
		}
		list.add(element);
	}

	public boolean exists(E element) {
		return ObjectUtils.equals(element, getCurrent());
	}
}
