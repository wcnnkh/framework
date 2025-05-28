package run.soeasy.framework.core.exchange;

public interface Dispatcher<T> extends Publisher<T>, Listenable<T> {

	@Override
	default BatchDispatcher<T> batch() {
		return (FakeBatchDispatcher<T, Dispatcher<T>>) () -> this;
	}

}
