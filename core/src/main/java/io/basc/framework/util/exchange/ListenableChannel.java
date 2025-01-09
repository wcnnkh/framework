package io.basc.framework.util.exchange;

import io.basc.framework.util.collection.Elements;

public interface ListenableChannel<T> extends Listenable<T>, Channel<T> {

	public static interface BatchListenableChannel<T>
			extends ListenableChannel<Elements<T>>, BatchChannel<T>, BatchListenable<T> {

		@Override
		default ListenableChannel<T> single() {
			return (FakeSingleListenableChannel<T, BatchListenableChannel<T>>) (() -> this);
		}
	}

	@FunctionalInterface
	public static interface FakeBatchListenableChannel<T, W extends ListenableChannel<T>>
			extends BatchListenableChannel<T>, FakeBatchListenable<T, W>, FakeBatchChannel<T, W> {
	}

	@FunctionalInterface
	public static interface FakeSingleListenableChannel<T, W extends ListenableChannel<Elements<T>>>
			extends ListenableChannel<T>, FakeSingleListenable<T, W>, FakeSingleChannel<T, W> {
	}

	@Override
	default BatchListenableChannel<T> batch() {
		return (FakeBatchListenableChannel<T, ListenableChannel<T>>) (() -> this);
	}
}
