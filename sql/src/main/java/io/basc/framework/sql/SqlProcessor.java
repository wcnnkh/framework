package io.basc.framework.sql;

import io.basc.framework.util.stream.Callback;
import io.basc.framework.util.stream.Processor;

public interface SqlProcessor<S> {
	default void process(Callback<S, ? extends Throwable> callback) throws SqlException {
		process((s) -> {
			callback.call(s);
			return null;
		});
	}

	<T> T process(Processor<S, ? extends T, ? extends Throwable> processor) throws SqlException;
}
