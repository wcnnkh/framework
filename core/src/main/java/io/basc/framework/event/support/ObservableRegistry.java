package io.basc.framework.event.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import io.basc.framework.event.BroadcastEventDispatcher;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.Observable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Registration;
import io.basc.framework.util.Selector;

public class ObservableRegistry<T> extends DynamicElementRegistry<Observable<T>> implements Observable<T> {
	private final ObservableValue<T> value;
	private Selector<T> selector;

	public ObservableRegistry() {
		this(new CopyOnWriteArraySet<>(), new StandardBroadcastEventDispatcher<>(), new ObservableValue<>());
	}

	public ObservableRegistry(Collection<Observable<T>> elements,
			BroadcastEventDispatcher<ChangeEvent<Elements<Observable<T>>>> elementEventDispatcher,
			ObservableValue<T> value) {
		super(elements, elementEventDispatcher);
		Assert.requiredArgument(value != null, "value");
		this.value = value;
		elementEventDispatcher.registerListener((event) -> touchValue());
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
		for (Observable<T> observable : getElements()) {
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

		return selector.apply(values);
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
