package io.basc.framework.util.exchange.event;

import io.basc.framework.util.collection.Elements;
import io.basc.framework.util.exchange.AbstractChannel;
import io.basc.framework.util.exchange.ListenableChannel;
import io.basc.framework.util.exchange.Listener;
import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.register.Registry;
import io.basc.framework.util.register.container.ArrayListContainer;
import io.basc.framework.util.select.Dispatcher;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class EventDispatcher<T> extends AbstractChannel<T> implements ListenableChannel<T> {
	private final FakeBatchListenableChannel<T, ListenableChannel<T>> batch = () -> this;
	@NonNull
	private final Dispatcher<Listener<? super T>> dispatcher;

	@NonNull
	private final Registry<Listener<? super T>> registry;

	public EventDispatcher() {
		this(Dispatcher.identity(), new ArrayListContainer<>());
	}

	@Override
	public BatchListenableChannel<T> batch() {
		return batch;
	}

	@Override
	public Registration registerListener(Listener<T> listener) {
		return registry.register(listener);
	}

	public void syncPublish(T resource) {
		Elements<Listener<? super T>> elements = dispatcher.dispatch(registry);
		elements.forEach((e) -> e.accept(resource));
	}
}
