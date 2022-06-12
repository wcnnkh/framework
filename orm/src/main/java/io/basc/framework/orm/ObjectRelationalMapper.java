package io.basc.framework.orm;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.ObjectAccessFactoryRegistry;
import io.basc.framework.mapper.ObjectMapper;

public interface ObjectRelationalMapper extends ObjectRelationalFactory, ObjectMapper<Object, ConversionException>,
		ObjectAccessFactoryRegistry<ConversionException> {

	@Override
	default boolean isEntity(Class<?> entityClass, Field field, ParameterDescriptor descriptor) {
		if (field instanceof Property) {
			return ((Property) field).isEntity();
		}
		return isEntity(entityClass, descriptor);
	}
}
