package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.Wrapper;

@FunctionalInterface
public interface FakeSinglePublisher<T, W extends Publisher<Elements<T>>> extends Publisher<T>, Wrapper<W> {

	@Override
	default Receipt publish(T resource) {
		return getSource().publish(Elements.singleton(resource));
	}

}