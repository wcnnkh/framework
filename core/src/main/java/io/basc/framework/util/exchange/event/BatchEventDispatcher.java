package io.basc.framework.util.exchange.event;

import io.basc.framework.util.Elements;
import io.basc.framework.util.exchange.ListenableChannel;
import io.basc.framework.util.exchange.ListenableChannel.BatchListenableChannel;
import io.basc.framework.util.exchange.Listener;
import io.basc.framework.util.register.Registry;
import io.basc.framework.util.register.container.ArrayListContainer;
import io.basc.framework.util.select.Dispatcher;
import lombok.NonNull;

public class BatchEventDispatcher<T> extends EventDispatcher<Elements<T>> implements BatchListenableChannel<T> {
	private final FakeSingleListenableChannel<T, ListenableChannel<Elements<T>>> single = () -> this;

	public BatchEventDispatcher() {
		this(Dispatcher.identity(), new ArrayListContainer<>());
	}

	public BatchEventDispatcher(@NonNull Dispatcher<Listener<? super Elements<T>>> dispatcher,
			@NonNull Registry<Listener<? super Elements<T>>> registry) {
		super(dispatcher, registry);
	}

	@Override
	public ListenableChannel<T> single() {
		return single;
	}
}
