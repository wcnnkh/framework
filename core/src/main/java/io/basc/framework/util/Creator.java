package io.basc.framework.util;

public interface Creator<T, E extends Throwable> {
	T create() throws E;
}
