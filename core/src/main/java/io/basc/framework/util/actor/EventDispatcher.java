package io.basc.framework.util.actor;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Listener;
import io.basc.framework.util.Registration;
import io.basc.framework.util.register.Registry;
import io.basc.framework.util.register.container.ArrayListRegistry;
import io.basc.framework.util.select.Dispatcher;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class EventDispatcher<T> extends AbstractExchange<T> {
	private final FakeBatchExchange<T, Exchange<T>> batch = new FakeBatchExchange<>(this);
	@NonNull
	private final Dispatcher<Listener<? super T>> dispatcher;

	@NonNull
	private final Registry<Listener<? super T>> registry;

	public EventDispatcher() {
		this(Dispatcher.identity(), new ArrayListRegistry<>());
	}

	@Override
	public BatchExchange<T> batch() {
		return batch;
	}

	@Override
	public Registration registerListener(Listener<? super T> listener) {
		return registry.register(listener);
	}

	public void syncPublish(T resource) {
		Elements<Listener<? super T>> elements = registry.getElements();
		elements = dispatcher.dispatch(elements);
		elements.forEach((e) -> e.accept(resource));
	}
}
