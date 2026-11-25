package run.soeasy.framework.core.exchange;

import java.util.concurrent.TimeUnit;

import run.soeasy.framework.core.streaming.Streamable;

@FunctionalInterface
interface FakeBatchChannel<T, W extends Channel<T>> extends BatchChannel<T>, FakeBatchPublisher<T, W> {

	@Override
	default Signal publish(Streamable<T> resource) {
		return FakeBatchPublisher.super.publish(resource);
	}

	@Override
	default Signal publish(Streamable<T> resource, long timeout, TimeUnit timeUnit) {
		return Signal.batch(resource.map((e) -> getSource().publish(e, timeout, timeUnit)));
	}
}