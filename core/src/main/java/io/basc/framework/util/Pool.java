package io.basc.framework.util;

import java.util.function.Supplier;

public interface Pool<T> extends Supplier<T> {
	T get();

	void release(T resource);

	default <V, E extends Throwable> V process(Pipeline<? super T, ? extends V, ? extends E> processor) throws E {
		T resource = get();
		try {
			return processor.process(resource);
		} finally {
			release(resource);
		}
	}

	default <E extends Throwable> void consume(Endpoint<? super T, ? extends E> processor) throws E {
		process((e) -> {
			processor.process(e);
			return null;
		});
	}
}
