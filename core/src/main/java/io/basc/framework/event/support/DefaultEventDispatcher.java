package io.basc.framework.event.support;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventPushException;
import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.event.batch.BatchEventDispatcher;
import io.basc.framework.event.batch.BatchEventListener;
import io.basc.framework.lang.Nullable;
import io.basc.framework.register.Registration;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Wrapper;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.select.Selector;

public class DefaultEventDispatcher<T> implements BatchEventDispatcher<T> {
	private final CopyOnWriteArrayList<Wrapper<BatchEventListener<T>>> listeners = new CopyOnWriteArrayList<>();
	@Nullable
	private final Selector<BatchEventListener<T>> eventListenerSelector;
	@Nullable
	private Executor publishEventExecutor;

	public DefaultEventDispatcher(@Nullable Selector<BatchEventListener<T>> eventListenerSelector) {
		this.eventListenerSelector = eventListenerSelector;
	}

	public List<BatchEventListener<T>> getListeners() {
		return Collections
				.unmodifiableList(listeners.stream().map((e) -> e.getDelegateSource()).collect(Collectors.toList()));
	}

	public int getListenerCount() {
		return listeners.size();
	}

	@Override
	public final Registration registerListener(EventListener<T> eventListener) throws EventRegistrationException {
		return BatchEventDispatcher.super.registerListener(eventListener);
	}

	@Override
	public final void publishEvent(T event) throws EventPushException {
		BatchEventDispatcher.super.publishEvent(event);
	}

	@Override
	public Registration registerBatchListener(BatchEventListener<T> batchEventListener)
			throws EventRegistrationException {
		Assert.requiredArgument(batchEventListener != null, "batchEventListener");
		Wrapper<BatchEventListener<T>> wrapper = new Wrapper<>(batchEventListener);
		wrapper.setEqualsAndHashCode(UUID.randomUUID().toString());
		listeners.add(wrapper);
		return () -> listeners.remove(wrapper);
	}

	public Executor getPublishEventExecutor() {
		return publishEventExecutor;
	}

	public void setPublishEventExecutor(Executor publishEventExecutor) {
		this.publishEventExecutor = publishEventExecutor;
	}

	@Override
	public void publishBatchEvent(Elements<T> events) throws EventPushException {
		if (publishEventExecutor == null) {
			syncPublishEvent(events);
		} else {
			publishEventExecutor.execute(() -> syncPublishEvent(events));
		}
	}

	private void syncPublishEvent(Elements<T> events) {
		if (publishEventExecutor == null) {
			for (Wrapper<BatchEventListener<T>> wrapper : listeners) {
				wrapper.getDelegateSource().onEvent(events);
			}
		} else {
			BatchEventListener<T> listener = eventListenerSelector
					.apply(Elements.of(listeners).map((e) -> e.getDelegateSource()));
			if (listener == null) {
				return;
			}
			listener.onEvent(events);
		}
	}
}
