package run.soeasy.framework.core.exchange;

public interface ListenableChannel<T> extends Channel<T>, Dispatcher<T> {

	@Override
	default BatchListenableChannel<T> batch() {
		return (FakeBatchListenableChannel<T, ListenableChannel<T>>) (() -> this);
	}
}
