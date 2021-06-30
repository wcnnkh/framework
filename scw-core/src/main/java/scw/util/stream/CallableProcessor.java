package scw.util.stream;

public interface CallableProcessor<T, E extends Throwable> {
	T process() throws E;
	
	default <V> V process(Processor<T, ? extends V, ? extends E> processor) throws E {
		return processor.process(process());
	}

	default void process(Callback<T, ? extends E> callback) throws E {
		callback.call(process());
	}
}
