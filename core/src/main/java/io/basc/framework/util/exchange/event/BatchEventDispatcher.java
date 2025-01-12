package io.basc.framework.util.exchange.event;

import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.exchange.ListenableChannel;
import io.basc.framework.util.exchange.ListenableChannel.BatchListenableChannel;
import io.basc.framework.util.exchange.Listener;
import io.basc.framework.util.function.Filter;
import io.basc.framework.util.register.Registry;
import io.basc.framework.util.register.container.ArrayListContainer;
import lombok.NonNull;

public class BatchEventDispatcher<T> extends EventDispatcher<Elements<T>> implements BatchListenableChannel<T> {
	private final FakeSingleListenableChannel<T, ListenableChannel<Elements<T>>> single = () -> this;

	public BatchEventDispatcher() {
		this(new ArrayListContainer<>(), Filter.identity());
	}

	public BatchEventDispatcher(@NonNull Registry<Listener<? super Elements<T>>> registry,
			@NonNull Filter<Listener<? super Elements<T>>> filter) {
		super(registry, filter);
	}

	@Override
	public ListenableChannel<T> single() {
		return single;
	}
}
