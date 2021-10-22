package io.basc.framework.util.stream;

import java.util.concurrent.Callable;

/**
 * @see Callable
 * @author shuchaowen
 *
 * @param <T>
 * @param <E>
 */
@FunctionalInterface
public interface CallableProcessor<T, E extends Throwable> {
	T process() throws E;
	
	default <V> V process(Processor<T, ? extends V, ? extends E> processor) throws E {
		return processor.process(process());
	}

	default void process(ConsumerProcessor<T, ? extends E> callback) throws E {
		callback.process(process());
	}
}
