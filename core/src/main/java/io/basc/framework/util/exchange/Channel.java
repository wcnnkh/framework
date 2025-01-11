package io.basc.framework.util.exchange;

import java.util.concurrent.TimeUnit;

import io.basc.framework.util.collections.Elements;

public interface Channel<T> extends Publisher<T> {
	public static interface BatchChannel<T> extends Channel<Elements<T>>, BatchPublisher<T> {
		@Override
		default Channel<T> single() {
			return (FakeSingleChannel<T, BatchChannel<T>>) (() -> this);
		}
	}

	@FunctionalInterface
	public static interface FakeBatchChannel<T, W extends Channel<T>>
			extends BatchChannel<T>, FakeBatchPublisher<T, W> {

		@Override
		default Receipts<?> publish(Elements<T> resource) {
			return FakeBatchPublisher.super.publish(resource);
		}

		@Override
		default Receipt publish(Elements<T> resource, long timeout, TimeUnit timeUnit) {
			Elements<Receipt> elemnets = resource.map((e) -> getSource().publish(e, timeout, timeUnit)).toList();
			return Receipts.of(elemnets);
		}
	}

	@FunctionalInterface
	public static interface FakeSingleChannel<T, W extends Channel<Elements<T>>>
			extends Channel<T>, FakeSinglePublisher<T, W> {

		@Override
		default Receipt publish(T resource) {
			return FakeSinglePublisher.super.publish(resource);
		}

		@Override
		default Receipt publish(T resource, long timeout, TimeUnit timeUnit) {
			return getSource().publish(Elements.singleton(resource), timeout, timeUnit);
		}
	}

	/**
	 * Constant for sending a message without a prescribed timeout.
	 */
	long INDEFINITE_TIMEOUT = -1;

	@Override
	default BatchChannel<T> batch() {
		return (FakeBatchChannel<T, Channel<T>>) (() -> this);
	}

	@Override
	default Receipt publish(T resource) {
		return publish(resource, INDEFINITE_TIMEOUT, TimeUnit.MILLISECONDS);
	}

	Receipt publish(T resource, long timeout, TimeUnit timeUnit);
}
