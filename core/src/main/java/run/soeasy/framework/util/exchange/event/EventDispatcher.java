package run.soeasy.framework.util.exchange.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.util.collections.Elements;
import run.soeasy.framework.util.exchange.AbstractChannel;
import run.soeasy.framework.util.exchange.ListenableChannel;
import run.soeasy.framework.util.exchange.Listener;
import run.soeasy.framework.util.exchange.Registration;
import run.soeasy.framework.util.function.Filter;
import run.soeasy.framework.util.register.Registry;
import run.soeasy.framework.util.register.container.ArrayListContainer;

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
