package run.soeasy.framework.core.exchange;

@FunctionalInterface
interface FakeBatchDispatcher<T, W extends Dispatcher<T>>
		extends BatchDispatcher<T>, FakeBatchPublisher<T, W>, FakeBatchListenable<T, W> {
}