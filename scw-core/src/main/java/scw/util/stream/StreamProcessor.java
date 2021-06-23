package scw.util.stream;

import scw.lang.Nullable;

public interface StreamProcessor<T, E extends Throwable> extends CallableProcessor<T, E> {
	@Nullable
	T process() throws E;
	
	<S> StreamProcessor<S, E> map(Processor<T, S, E> processor);

	StreamProcessor<T, E> onClose(CallbackProcessor<E> closeProcessor);

	void close() throws E;
}
