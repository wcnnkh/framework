package io.basc.framework.factory;

import io.basc.framework.util.Creator;

public interface InstanceCreator<T, E extends Throwable> extends Creator<T, E> {
	T create() throws E;

	T create(Class<?>[] parameterTypes, Object[] params) throws E;
}
