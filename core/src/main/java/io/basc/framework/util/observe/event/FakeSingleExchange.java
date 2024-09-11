package io.basc.framework.util.observe.event;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Wrapper;
import io.basc.framework.util.observe.Listener;
import io.basc.framework.util.observe.Receipt;
import io.basc.framework.util.observe.Registration;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FakeSingleExchange<T, W extends Exchange<Elements<T>>> implements Exchange<T>, Wrapper<W> {
	@NonNull
	private final W exchange;

	@Override
	public Registration registerListener(Listener<? super T> listener) {
		FakeBatchListener<T, Listener<? super T>> elementsListener = new FakeBatchListener<>(listener);
		return exchange.registerListener(elementsListener);
	}

	@Override
	public Receipt publish(T resource) {
		return exchange.publish(Elements.singleton(resource));
	}

	@Override
	public W getSource() {
		return exchange;
	}

}
