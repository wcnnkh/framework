package io.basc.framework.util.event.support;

import java.util.concurrent.Executor;
import java.util.function.Function;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.event.EventDispatcher;
import io.basc.framework.util.event.EventListener;
import io.basc.framework.util.event.EventPublishService;
import io.basc.framework.util.event.EventPushException;
import io.basc.framework.util.event.EventRegistrationException;
import io.basc.framework.util.event.EventRegistry;
import io.basc.framework.util.event.NamedEventDispatcher;
import io.basc.framework.util.function.ConsumeProcessor;
import io.basc.framework.util.match.IdentityMatcher;
import io.basc.framework.util.match.Matcher;
import io.basc.framework.util.register.KeyValueRegistry;
import io.basc.framework.util.register.Registration;
import lombok.NonNull;

public class DefaultNamedEventDispatcher<K, T> implements NamedEventDispatcher<K, T> {
	@NonNull
	private final KeyValueRegistry<K, EventDispatcher<T>> dispatcherRegistry;
	@NonNull
	private final Function<? super K, ? extends EventDispatcher<T>> eventDispatcherFactory;
	@NonNull
	private final Matcher<? super K> matcher;
	private Executor publishEventExecutor;

	public DefaultNamedEventDispatcher(@NonNull KeyValueRegistry<K, EventDispatcher<T>> dispatcherRegistry,
			@NonNull Function<? super K, ? extends EventDispatcher<T>> eventDispatcherFactory,
			@NonNull Matcher<? super K> matcher) {
		this.dispatcherRegistry = dispatcherRegistry;
		this.eventDispatcherFactory = eventDispatcherFactory;
		this.matcher = matcher;
	}

	public Elements<EventRegistry<T>> getEventRegistrys(K name) {
		Elements<KeyValue<K, EventDispatcher<T>>> elements = dispatcherRegistry.getElements(name,
				IdentityMatcher.getInstance());
		if (elements.isEmpty()) {
			// 不存在
			EventDispatcher<T> dispatcher = eventDispatcherFactory.apply(name);
			Registration registration = dispatcherRegistry.register(name, dispatcher);
			if (registration.isInvalid()) {
				// 注册失败, 重新执行
				return getEventRegistrys(name);
			}
			return Elements.singleton(dispatcher);
		}
		return elements.map((e) -> e.getValue());
	}

	@Override
	public Registration registerBatchEventsListener(K name, EventListener<Elements<T>> batchEventsListener)
			throws EventRegistrationException {
		Elements<EventRegistry<T>> registrys = getEventRegistrys(name);
		return Registration.registers(registrys, (e) -> e.registerBatchEventsListener(batchEventsListener));
	}

	public Elements<EventPublishService<T>> getPublishServices(K name) {
		return dispatcherRegistry.getElements(name, matcher).map((e) -> e.getValue());
	}

	@Override
	public final void publishEvent(K name, T event) throws EventPushException {
		NamedEventDispatcher.super.publishEvent(name, event);
	}

	@Override
	public void publishBatchEvents(K name, Elements<T> events) {
		if (publishEventExecutor == null) {
			syncPublishBatchEvent(name, events);
		} else {
			publishEventExecutor.execute(() -> syncPublishBatchEvent(name, events));
		}
	}

	protected void syncPublishBatchEvent(K name, Elements<T> events) {
		ConsumeProcessor.consumeAll(getPublishServices(name), (e) -> e.publishBatchEvents(events));
	}
}
