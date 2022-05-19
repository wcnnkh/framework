package io.basc.framework.mapper;

import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.stream.Processor;

public interface ObjectMapper<S, E extends Throwable> {
	boolean isMapperRegistred(Class<?> type);

	@Nullable
	<T> Processor<S, T, E> getMappingProcessor(Class<? extends T> type);

	<T> void registerMapper(Class<T> type,
			Processor<S, ? extends T, ? extends E> processor);

	default <T> T process(Class<T> type, S source) throws E,
			NotSupportedException {
		Processor<S, T, E> processor = getMappingProcessor(type);
		if (processor == null) {
			throw new NotSupportedException(type.getName());
		}
		return processor.process(source);
	}
}
