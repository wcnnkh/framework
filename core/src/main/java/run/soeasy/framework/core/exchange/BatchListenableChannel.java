package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.Elements;

public interface BatchListenableChannel<T>
		extends ListenableChannel<Elements<T>>, BatchChannel<T>, BatchDispatcher<T> {

	@Override
	default ListenableChannel<T> single() {
		return (FakeSingleListenableChannel<T, BatchListenableChannel<T>>) (() -> this);
	}
}