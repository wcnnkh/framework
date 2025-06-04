package run.soeasy.framework.core.exchange;

import java.util.concurrent.TimeUnit;

import run.soeasy.framework.core.collection.Elements;

@FunctionalInterface
public interface FakeSingleChannel<T, W extends Channel<Elements<T>>> extends Channel<T>, FakeSinglePublisher<T, W> {

	@Override
	default Receipt publish(T resource) {
		return FakeSinglePublisher.super.publish(resource);
	}

	@Override
	default Receipt publish(T resource, long timeout, TimeUnit timeUnit) {
		return getSource().publish(Elements.singleton(resource), timeout, timeUnit);
	}
}