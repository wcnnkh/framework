package run.soeasy.framework.core.exchange;

import java.util.concurrent.TimeUnit;

import run.soeasy.framework.core.streaming.Streamable;

@FunctionalInterface
interface FakeSingleChannel<T, W extends Channel<Streamable<T>>> extends Channel<T>, FakeSinglePublisher<T, W> {

	@Override
	default Operation publish(T resource) {
		return FakeSinglePublisher.super.publish(resource);
	}

	@Override
	default Operation publish(T resource, long timeout, TimeUnit timeUnit) {
		return getSource().publish(Streamable.singleton(resource), timeout, timeUnit);
	}
}