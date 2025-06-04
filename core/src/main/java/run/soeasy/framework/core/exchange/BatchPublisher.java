package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.Elements;

public interface BatchPublisher<T> extends Publisher<Elements<T>> {

	default Publisher<T> single() {
		return (FakeSinglePublisher<T, BatchPublisher<T>>) (() -> this);
	}
}