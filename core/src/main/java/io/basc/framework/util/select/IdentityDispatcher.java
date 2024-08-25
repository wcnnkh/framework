package io.basc.framework.util.select;

import java.util.function.Function;

import io.basc.framework.util.element.Elements;

public class IdentityDispatcher<E> implements Dispatcher<E> {
	private static volatile IdentityDispatcher<?> instance;

	public static IdentityDispatcher<?> getInstance() {
		if (instance == null) {
			synchronized (IdentityDispatcher.class) {
				if (instance == null) {
					instance = new IdentityDispatcher<>();
				}
			}
		}
		return instance;
	}

	@Override
	public Elements<E> dispatch(Elements<? extends E> elements) {
		return elements == null ? null : elements.map(Function.identity());
	}
}
