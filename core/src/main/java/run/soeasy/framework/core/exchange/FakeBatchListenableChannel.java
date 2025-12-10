package run.soeasy.framework.core.exchange;

@FunctionalInterface
interface FakeBatchListenableChannel<T, W extends ListenableChannel<T>>
		extends BatchListenableChannel<T>, FakeBatchChannel<T, W>, FakeBatchDispatcher<T, W> {
}