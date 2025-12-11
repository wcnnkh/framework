package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.streaming.Streamable;

@FunctionalInterface
interface FakeSinglePublisher<T, W extends Publisher<Streamable<T>>> extends Publisher<T>, Wrapper<W> {

	@Override
	default Operation publish(T resource) {
		return getSource().publish(Streamable.singleton(resource));
	}

}