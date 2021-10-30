package io.basc.framework.sql.orm;

import java.util.stream.Stream;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.mapper.Fields;
import io.basc.framework.orm.ObjectRelationalProcessor;

public interface TableStructureProcessor extends ObjectRelationalProcessor {
	Column resolve(Class<?> entityClass, Field field);

	TableMetadata resolveMetadata(Class<?> entityClass);

	ColumnMetadata resolveMetadata(Class<?> entityClass, FieldDescriptor fieldDescriptor);

	default Stream<Column> map(Class<?> entityClass, Fields fields) {
		return fields.stream().map((field) -> resolve(entityClass, field));
	}
}
