package io.basc.framework.convert;

import io.basc.framework.util.stream.Processor;

public class IdentityConverter<T, E extends Throwable> implements Processor<T, T, E> {

	@Override
	public T process(T source) throws E {
		return source;
	}
}
