package io.basc.framework.util.observe.event;

import io.basc.framework.util.Elements;
import io.basc.framework.util.observe.Listener;
import io.basc.framework.util.observe.Registration;
import io.basc.framework.util.observe.register.Registry;
import io.basc.framework.util.observe.register.container.ArrayListRegistry;
import io.basc.framework.util.select.Dispatcher;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class EventDispatcher<T> extends AbstractExchange<Elements<T>> implements BatchExchange<T> {
	@NonNull
	private final Dispatcher<Listener<? super Elements<T>>> dispatcher;
	@NonNull
	private final Registry<Listener<? super Elements<T>>> registry;
	private final FakeSingleExchange<T, Exchange<Elements<T>>> single = new FakeSingleExchange<>(this);

	public EventDispatcher() {
		this(Dispatcher.identity(), new ArrayListRegistry<>());
	}

	public void syncPublish(Elements<T> resource) {
		Elements<Listener<? super Elements<T>>> elements = registry.getElements();
		elements = dispatcher.dispatch(elements);
		elements.forEach((e) -> e.accept(resource));
	}

	@Override
	public Registration registerListener(Listener<? super Elements<T>> listener) {
		return registry.register(listener);
	}

	@Override
	public Exchange<T> single() {
		return single;
	}
}
