package io.basc.framework.util.stream;

public interface DoubleToDoubleProcessor<E extends Throwable> {
	double process(double source) throws E;
}
