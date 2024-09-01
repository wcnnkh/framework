package io.basc.framework.util.event.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.event.EventDispatcher;
import io.basc.framework.util.event.EventListener;
import io.basc.framework.util.event.EventPushException;
import io.basc.framework.util.event.EventRegistrationException;
import io.basc.framework.util.event.empty.EmptyEventDispatcher;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.register.Registry;
import io.basc.framework.util.register.container.ElementRegistration;
import io.basc.framework.util.register.container.ElementRegistry;
import io.basc.framework.util.select.Dispatcher;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * 默认的事件分发器实现
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
@Getter
@Setter
public class DefaultEventDispatcher<T> implements EventDispatcher<T> {
	@NonNull
	private final Registry<EventListener<Elements<T>>> eventListenerRegistry;
	@NonNull
	private final Dispatcher<EventListener<Elements<T>>> eventListenerDispatcher;
	private Executor publishEventExecutor;

	/**
	 * 默认构造一个可重复注册的广播事件
	 */
	public DefaultEventDispatcher() {
		this(ArrayList::new, Dispatcher.identity());
	}

	public DefaultEventDispatcher(
			Supplier<? extends Collection<ElementRegistration<EventListener<Elements<T>>>>> eventListenerSupplier,
			@NonNull Dispatcher<EventListener<Elements<T>>> eventListenerDispatcher) {
		this(new ElementRegistry<>(eventListenerSupplier, EmptyEventDispatcher.empty()), eventListenerDispatcher);
	}

	public DefaultEventDispatcher(@NonNull Registry<EventListener<Elements<T>>> eventListenerRegistry,
			@NonNull Dispatcher<EventListener<Elements<T>>> eventListenerDispatcher) {
		this.eventListenerRegistry = eventListenerRegistry;
		this.eventListenerDispatcher = eventListenerDispatcher;
	}

	@Override
	public final Registration registerEventListener(EventListener<T> eventListener) throws EventRegistrationException {
		return EventDispatcher.super.registerEventListener(eventListener);
	}

	@Override
	public final void publishEvent(T event) throws EventPushException {
		EventDispatcher.super.publishEvent(event);
	}

	@Override
	public Registration registerBatchEventsListener(EventListener<Elements<T>> batchEvenstListener)
			throws EventRegistrationException {
		Assert.requiredArgument(batchEvenstListener != null, "batchEvenstListener");
		return eventListenerRegistry.register(batchEvenstListener);
	}

	@Override
	public void publishBatchEvents(Elements<T> events) throws EventPushException {
		if (publishEventExecutor == null) {
			syncPublishEvents(events);
		} else {
			publishEventExecutor.execute(() -> syncPublishEvents(events));
		}
	}

	private void syncPublishEvents(Elements<T> events) {
		Elements<EventListener<Elements<T>>> selected = eventListenerRegistry.getElements().dispatch(eventListenerDispatcher);
		selected.forEach((e) -> e.onEvent(events));
	}
}
