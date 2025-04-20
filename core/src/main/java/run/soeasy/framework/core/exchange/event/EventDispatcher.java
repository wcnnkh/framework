package run.soeasy.framework.core.exchange.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.AbstractChannel;
import run.soeasy.framework.core.exchange.ListenableChannel;
import run.soeasy.framework.core.exchange.Listener;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.container.ArrayListContainer;
import run.soeasy.framework.core.exchange.container.Registry;
import run.soeasy.framework.core.exe.Filter;

@RequiredArgsConstructor
@Getter
@Setter
public class EventDispatcher<T> extends AbstractChannel<T> implements ListenableChannel<T> {
	private final FakeBatchListenableChannel<T, ListenableChannel<T>> batch = () -> this;
	@NonNull
	private final Registry<Listener<? super T>> registry;
	@NonNull
	private final Filter<Listener<? super T>> filter;

	public EventDispatcher() {
		this(new ArrayListContainer<>(), Filter.identity());
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
		Elements<Listener<? super T>> elements = filter.apply(registry);
		elements.forEach((e) -> e.accept(resource));
	}
}
