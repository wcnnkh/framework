package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.Elements;

public interface BatchDispatcher<T> extends Dispatcher<Elements<T>>, BatchPublisher<T>, BatchListenable<T> {

	@Override
	default Dispatcher<T> single() {
		return (FakeSingleDispatcher<T, BatchDispatcher<T>>) () -> this;
	}
}