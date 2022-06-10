package io.basc.framework.orm;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.mapper.ObjectAccessFactoryRegistry;
import io.basc.framework.mapper.ObjectMapper;

public interface ObjectRelationalMapper
		extends ObjectRelationalFactory, ObjectMapper<Object, OrmException>, ObjectAccessFactoryRegistry<OrmException> {

	@Override
	default boolean isEntity(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ObjectMapper.super.isEntity(entityClass, descriptor);
	}
}
