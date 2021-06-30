package scw.sql;

import scw.util.stream.Callback;
import scw.util.stream.Processor;

public interface SqlProcessor<S> {
	default void process(Callback<S, ? extends Throwable> callback) throws SqlException {
		process((s) -> {
			callback.call(s);
			return null;
		});
	}

	<T> T process(Processor<S, ? extends T, ? extends Throwable> processor) throws SqlException;
}
