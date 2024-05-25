package io.basc.framework.mapper.access;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.transform.ReversibleTransformer;

public interface ObjectAccessFactory<T> extends ReversibleTransformer<S, Object, MappingException> {
	default ObjectAccess getObjectAccess(T source) {
		return getObjectAccess(source, TypeDescriptor.forObject(source));
	}

	ObjectAccess getObjectAccess(T source, TypeDescriptor sourceType);
}