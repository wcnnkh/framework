package io.basc.framework.util.actor;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Listenable;
import io.basc.framework.util.Listener;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.Receipt;
import io.basc.framework.util.Receipts;
import io.basc.framework.util.Registration;
import io.basc.framework.util.Wrapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface Exchange<T> extends Listenable<T>, Publisher<T> {

	@RequiredArgsConstructor
	public class FakeSingleListener<T, W extends Listener<? super Elements<T>>> implements Listener<T>, Wrapper<W> {
		@NonNull
		private final W listener;

		@Override
		public void accept(T source) {
			listener.accept(Elements.singleton(source));
		}

		@Override
		public W getSource() {
			return listener;
		}
	}

	/**
	 * 假的批处理
	 * 
	 * @author shuchaowen
	 *
	 * @param <T>
	 */
	@RequiredArgsConstructor
	public static final class FakeBatchExchange<T, W extends Exchange<T>> implements BatchExchange<T>, Wrapper<W> {
		@NonNull
		private final W exchange;

		@Override
		public Registration registerListener(Listener<? super Elements<T>> listener) {
			FakeSingleListener<T, Listener<? super Elements<T>>> singletonSourceListener = new FakeSingleListener<>(
					listener);
			return exchange.registerListener(singletonSourceListener);
		}

		@Override
		public W getSource() {
			return exchange;
		}

		@Override
		public Receipts<?> publish(Elements<T> resource) {
			Elements<Receipt> elemnets = resource.map((e) -> exchange.publish(e)).toList();
			return Receipts.of(elemnets);
		}

	}

	/**
	 * 批处理
	 * 
	 * @return 返回一个批处理的视图
	 */
	default BatchExchange<T> batch() {
		return new FakeBatchExchange<>(this);
	}
}
