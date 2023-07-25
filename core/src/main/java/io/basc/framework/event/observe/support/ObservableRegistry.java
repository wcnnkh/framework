package io.basc.framework.event.observe.support;

import java.util.ArrayList;
import java.util.List;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.observe.Observable;
import io.basc.framework.event.support.DefaultDynamicElementRegistry;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.registry.Registration;
import io.basc.framework.util.select.Selector;

public class ObservableRegistry<T> extends DefaultDynamicElementRegistry<Observable<? extends T>> implements Observable<T> {
	private final ObservableValue<T> value = new ObservableValue<>();
	private Selector<T> selector;

	public ObservableRegistry() {
		getElementEventDispatcher().registerListener((event) -> touchValue());
	}

	public ObservableValue<T> getValue() {
		return value;
	}

	public final Selector<T> getSelector() {
		return selector;
	}

	@Override
	public T orElse(T other) {
		return value.orElse(other);
	}

	@Override
	public Registration registerListener(EventListener<ChangeEvent<T>> eventListener) {
		return value.registerListener(eventListener);
	}

	protected T select() {
		if (selector == null) {
			for (Observable<? extends T> observable : getElements()) {
				if (observable == null || observable == this) {
					continue;
				}

				T value = observable.orElse(null);
				if (value != null) {
					return value;
				}
			}
			return null;
		}

		List<T> values = new ArrayList<>();
		for (Observable<? extends T> observable : getElements()) {
			if (observable == null || observable == this) {
				continue;
			}

			observable.ifPresent(values::add);
		}

		if (values.isEmpty()) {
			return null;
		}

		if (values.size() == 1) {
			return values.get(0);
		}

		return selector.apply(Elements.of(values));
	}

	public void setSelector(Selector<T> selector) {
		this.selector = selector;
		touchValue();
	}

	protected T touchValue() {
		return value.set(select());
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
