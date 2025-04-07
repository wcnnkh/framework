package run.soeasy.framework.util.exchange;

import run.soeasy.framework.util.collection.Elements;

public interface ListenableChannel<T> extends Channel<T>, Dispatcher<T> {

	public static interface BatchListenableChannel<T>
			extends ListenableChannel<Elements<T>>, BatchChannel<T>, BatchDispatcher<T> {

		@Override
		default ListenableChannel<T> single() {
			return (FakeSingleListenableChannel<T, BatchListenableChannel<T>>) (() -> this);
		}
	}

	@FunctionalInterface
	public static interface FakeBatchListenableChannel<T, W extends ListenableChannel<T>>
			extends BatchListenableChannel<T>, FakeBatchChannel<T, W>, FakeBatchDispatcher<T, W> {
	}

	@FunctionalInterface
	public static interface FakeSingleListenableChannel<T, W extends ListenableChannel<Elements<T>>>
			extends ListenableChannel<T>, FakeSingleChannel<T, W>, FakeSingleDispatcher<T, W> {
	}

	@Override
	default BatchListenableChannel<T> batch() {
		return (FakeBatchListenableChannel<T, ListenableChannel<T>>) (() -> this);
	}
}
