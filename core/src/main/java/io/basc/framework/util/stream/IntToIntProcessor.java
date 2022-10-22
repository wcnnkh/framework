package io.basc.framework.util.stream;

public interface IntToIntProcessor<E extends Throwable> {
	int process(int source) throws E;
}
