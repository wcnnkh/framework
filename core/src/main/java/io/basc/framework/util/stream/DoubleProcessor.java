package io.basc.framework.util.stream;

public interface DoubleProcessor<T, E extends Throwable> {
	T process(double source) throws E;
}
