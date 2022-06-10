package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;

public interface ObjectAccessFactory<S, E extends Throwable> {
	default ObjectAccess<E> getObjectAccess(S source) throws E {
		return getObjectAccess(source, TypeDescriptor.forObject(source));
	}

	ObjectAccess<E> getObjectAccess(S source, TypeDescriptor sourceType) throws E;
}