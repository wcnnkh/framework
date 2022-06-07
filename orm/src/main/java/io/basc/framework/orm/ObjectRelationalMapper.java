package io.basc.framework.orm;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.mapper.ObjectMapper;

public interface ObjectRelationalMapper<S, E extends Throwable> extends ObjectRelationalFactory, ObjectMapper<S, E> {

	@Override
	default Boolean isEntity(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ObjectMapper.super.isEntity(entityClass, descriptor);
	}
}
