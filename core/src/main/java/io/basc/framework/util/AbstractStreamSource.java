package io.basc.framework.util;

public abstract class AbstractStreamSource<T, E extends Throwable, C extends StreamSource<T, E>>
		extends StandardCloser<T, E, C> implements StreamSource<T, E> {
}
