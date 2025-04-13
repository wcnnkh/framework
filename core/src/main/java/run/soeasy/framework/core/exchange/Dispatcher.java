package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.Elements;

public interface Dispatcher<T> extends Publisher<T>, Listenable<T> {

	@Override
	default BatchDispatcher<T> batch() {
		return (FakeBatchDispatcher<T, Dispatcher<T>>) () -> this;
	}

	@FunctionalInterface
	public static interface FakeBatchDispatcher<T, W extends Dispatcher<T>>
			extends BatchDispatcher<T>, FakeBatchPublisher<T, W>, FakeBatchListenable<T, W> {
	}

	@FunctionalInterface
	public static interface FakeSingleDispatcher<T, W extends Dispatcher<Elements<T>>>
			extends Dispatcher<T>, FakeSinglePublisher<T, W>, FakeSingleListenable<T, W> {
	}

	public static interface BatchDispatcher<T> extends Dispatcher<Elements<T>>, BatchPublisher<T>, BatchListenable<T> {

		@Override
		default Dispatcher<T> single() {
			return (FakeSingleDispatcher<T, BatchDispatcher<T>>) () -> this;
		}
	}
}
