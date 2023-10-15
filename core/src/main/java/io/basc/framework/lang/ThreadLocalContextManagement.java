package io.basc.framework.lang;

import java.util.LinkedList;

import io.basc.framework.util.registry.Registration;

/**
 * 线程上下文管理
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public class ThreadLocalContextManagement<T> extends NamedThreadLocal<LinkedList<T>> {

	public ThreadLocalContextManagement(String name) {
		super(name);
	}

	@Nullable
	public T getContext() {
		LinkedList<T> list = get();
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.getLast();
	}

	public Registration register(T context) {
		LinkedList<T> list = get();
		if (list == null) {
			list = new LinkedList<>();
		}
		list.add(context);
		set(list);
		return () -> {
			LinkedList<T> oldList = get();
			if (oldList == null || oldList.isEmpty()) {
				throw new UnsupportedException("No context[" + getName() + "] exists");
			}

			T last = oldList.getLast();
			if (last != context) {
				throw new UnsupportedException(
						"Inconsistent sequence of context[" + getName() + "] registration and deregistration");
			}
			oldList.removeLast();
		};
	}
}
