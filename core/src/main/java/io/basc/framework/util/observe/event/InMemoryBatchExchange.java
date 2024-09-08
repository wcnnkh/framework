package io.basc.framework.util.observe.event;

import java.util.concurrent.Executor;

import io.basc.framework.util.Elements;
import io.basc.framework.util.observe.Listenable;
import io.basc.framework.util.observe.Listener;
import io.basc.framework.util.observe.Receipt;
import io.basc.framework.util.observe.Registration;
import io.basc.framework.util.observe.Registry;
import io.basc.framework.util.select.Dispatcher;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class InMemoryBatchExchange<T> implements BatchExchange<T> {
	@NonNull
	private final Dispatcher<Elements<Listener<? super Elements<T>>>> dispatcher;
	private Executor publishExecutor;
	@NonNull
	private final Registry<Listener<? super Elements<T>>> registry;
	private final FakeSingleExchange<T, Exchange<Elements<T>>> single = new FakeSingleExchange<>(this);

	@Override
	public Listenable<? extends Receipt> publish(Elements<T> resource) {

		if (publishExecutor == null) {

		} else {

		}
		// TODO Auto-generated method stub
		return null;
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
