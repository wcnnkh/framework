package io.basc.framework.util.stream;

public interface LongProcessor<T, E extends Throwable> {
	T process(long source) throws E;
}
