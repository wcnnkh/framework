package run.soeasy.framework.core.exchange.event;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.Filter;
import run.soeasy.framework.core.exchange.ListenableChannel;
import run.soeasy.framework.core.exchange.ListenableChannel.BatchListenableChannel;
import run.soeasy.framework.core.exchange.Listener;
import run.soeasy.framework.core.exchange.container.ArrayListContainer;
import run.soeasy.framework.core.exchange.container.Registry;

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
