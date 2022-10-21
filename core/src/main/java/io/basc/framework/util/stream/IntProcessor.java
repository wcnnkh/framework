package io.basc.framework.util.stream;

public interface IntProcessor<T, E extends Throwable> {
	T process(double source) throws E;
}
