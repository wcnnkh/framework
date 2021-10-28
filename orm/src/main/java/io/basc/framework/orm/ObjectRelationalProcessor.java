package io.basc.framework.orm;

import java.util.stream.Stream;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.mapper.Fields;

public interface ObjectRelationalProcessor {
	EntityMetadata resolveMetadata(Class<?> entityClass);

	PropertyMetadata resolveMetadata(Class<?> entityClass, FieldDescriptor fieldDescriptor);

	Property resolve(Class<?> entityClass, Field field);

	default Stream<? extends Property> map(Class<?> entityClass, Fields fields) {
		return fields.stream().map((field) -> resolve(entityClass, field));
	}
}
