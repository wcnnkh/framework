package io.basc.framework.event.observe.support;

import java.util.concurrent.atomic.AtomicReference;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.ChangeType;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.event.broadcast.BroadcastEventDispatcher;
import io.basc.framework.event.broadcast.support.StandardBroadcastEventDispatcher;
import io.basc.framework.event.observe.Observable;
import io.basc.framework.event.observe.ObservableChangeEvent;
import io.basc.framework.util.Assert;
import io.basc.framework.util.registry.Registration;

public class ObservableValue<T> implements Observable<T> {
	private final BroadcastEventDispatcher<ChangeEvent<T>> eventDispatcher;
	private final AtomicReference<T> value = new AtomicReference<>();

	public ObservableValue() {
		this(new StandardBroadcastEventDispatcher<>());
	}

	public ObservableValue(BroadcastEventDispatcher<ChangeEvent<T>> eventDispatcher) {
		Assert.requiredArgument(eventDispatcher != null, "eventDispatcher");
		this.eventDispatcher = eventDispatcher;
	}

	@Override
	public T orElse(T other) {
		T value = this.value.get();
		return value == null ? other : value;
	}

	public BroadcastEventDispatcher<ChangeEvent<T>> getEventDispatcher() {
		return eventDispatcher;
	}

	@Override
	public Registration registerListener(EventListener<ChangeEvent<T>> eventListener)
			throws EventRegistrationException {
		return eventDispatcher.registerListener(eventListener);
	}

	private void publishEvent(T oldValue, T newValue) {
		if (oldValue == newValue) {
			return;
		}

		ChangeType changeType;
		if (oldValue == null) {
			changeType = ChangeType.CREATE;
		} else if (newValue == null) {
			changeType = ChangeType.DELETE;
		} else {
			changeType = ChangeType.UPDATE;
		}
		ObservableChangeEvent<T> changeEvent = new ObservableChangeEvent<T>(changeType, oldValue, newValue);
		eventDispatcher.publishEvent(changeEvent);
	}

	public T set(T newValue) {
		T oldValue = value.getAndSet(newValue);
		publishEvent(oldValue, newValue);
		return oldValue;
	}

	public boolean set(T oldValue, T newValue) {
		if (value.compareAndSet(oldValue, newValue)) {
			publishEvent(oldValue, newValue);
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
