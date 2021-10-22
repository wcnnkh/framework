package io.basc.framework.sql;

import io.basc.framework.util.stream.ConsumerProcessor;
import io.basc.framework.util.stream.Processor;

public interface SqlProcessor<S> {
	default void process(ConsumerProcessor<S, ? extends Throwable> processor) throws SqlException {
		process(processor.toProcessor());
	}

	<T> T process(Processor<S, ? extends T, ? extends Throwable> processor) throws SqlException;
}
