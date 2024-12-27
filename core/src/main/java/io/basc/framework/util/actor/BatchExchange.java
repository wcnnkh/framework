package io.basc.framework.util.actor;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Receipt;
import io.basc.framework.util.Registration;
import io.basc.framework.util.Wrapper;
import io.basc.framework.util.exchange.Listener;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface BatchExchange<T> extends Exchange<Elements<T>> {

	@RequiredArgsConstructor
	public static class FakeBatchListener<T, W extends Listener<? super T>>
			implements Listener<Elements<T>>, Wrapper<Listener<? super T>> {
		@NonNull
		private final W listener;

		@Override
		public void accept(Elements<T> source) {
			source.forEach(listener);
		}

		@Override
		public Listener<? super T> getSource() {
			return listener;
		}

	}

	@RequiredArgsConstructor
	public static class FakeSingleExchange<T, W extends Exchange<Elements<T>>> implements Exchange<T>, Wrapper<W> {
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

	/**
	 * 一个个处理
	 * 
	 * @return
	 */
	default Exchange<T> single() {
		return new FakeSingleExchange<>(this);
	}
}
