package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;

public interface ObjectAccessFactory<T> {
	default ObjectAccess getObjectAccess(T source) {
		return getObjectAccess(source, TypeDescriptor.forObject(source));
	}

	ObjectAccess getObjectAccess(T source, TypeDescriptor sourceType);
}