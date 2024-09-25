package io.basc.framework.util.actor;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Listener;
import io.basc.framework.util.Receipt;
import io.basc.framework.util.Receipts;
import io.basc.framework.util.Registration;
import io.basc.framework.util.Wrapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 假的批处理
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
@RequiredArgsConstructor
public final class FakeBatchExchange<T, W extends Exchange<T>> implements BatchExchange<T>, Wrapper<W> {
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
