package io.basc.framework.mapper;

import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.stream.Processor;

public interface Mapper<S, E extends Throwable> {
	boolean isRegistred(Class<?> type);

	@Nullable
	<T> Processor<S, T, E> getProcessor(Class<? extends T> type);

	<T> void register(Class<T> type, Processor<S, ? extends T, ? extends E> processor);

	default <T> T process(Class<T> type, S source) throws E, NotSupportedException {
		Processor<S, T, E> processor = getProcessor(type);
		if (processor == null) {
			throw new NotSupportedException(type.getName());
		}
		return processor.process(source);
	}
}
