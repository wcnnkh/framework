package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.Elements;

public interface BatchChannel<T> extends Channel<Elements<T>>, BatchPublisher<T> {
	@Override
	default Channel<T> single() {
		return (FakeSingleChannel<T, BatchChannel<T>>) (() -> this);
	}
}
