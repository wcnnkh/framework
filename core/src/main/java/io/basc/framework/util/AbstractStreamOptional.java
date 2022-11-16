package io.basc.framework.util;

public abstract class AbstractStreamOptional<T, C extends StreamOptional<T>>
		extends AbstractStreamOperations<T, RuntimeException, C> implements StreamOptional<T> {
}
