package io.basc.framework.util.event;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Listener;
import io.basc.framework.util.register.Registry;
import io.basc.framework.util.register.container.ArrayListRegistry;
import io.basc.framework.util.select.Dispatcher;
import lombok.NonNull;

public class EventsDispatcher<T> extends EventDispatcher<Elements<T>> implements BatchExchange<T> {
	private final FakeSingleExchange<T, Exchange<Elements<T>>> single = new FakeSingleExchange<>(this);

	public EventsDispatcher() {
		this(Dispatcher.identity(), new ArrayListRegistry<>());
	}

	public EventsDispatcher(@NonNull Dispatcher<Listener<? super Elements<T>>> dispatcher,
			@NonNull Registry<Listener<? super Elements<T>>> registry) {
		super(dispatcher, registry);
	}

	@Override
	public Exchange<T> single() {
		return single;
	}
}
