package io.basc.framework.util.stream;

public interface LongToLongProcessor<E extends Throwable> {
	long process(long source) throws E;
}
