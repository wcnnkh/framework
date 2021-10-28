package io.basc.framework.sql.orm;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.mapper.Fields;
import io.basc.framework.orm.ObjectRelationalMapping;

public interface TableMapping extends TableStructureProcessor, ObjectRelationalMapping, TableResolver {

	@Override
	default Column resolve(Class<?> entityClass, Field field) {
		return new DefaultColumn(this, entityClass, field, this);
	}

	@Override
	default ColumnMetadata resolveMetadata(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		return new DefaultColumnMetdata(this, entityClass, fieldDescriptor, this);
	}

	@Override
	default TableMetadata resolveMetadata(Class<?> entityClass) {
		return new DefaultTableMetadata(this, entityClass, this);
	}

	@Override
	default TableStructure getStructure(Class<?> entityClass) {
		return getStructure(entityClass, getFields(entityClass));
	}

	@Override
	default TableStructure getStructure(Class<?> entityClass, Field parentField) {
		return getStructure(entityClass, getFields(entityClass, parentField));
	}

	@Override
	default TableStructure getStructure(Class<?> entityClass, Fields fields) {
		return new DefaultTableStructure(this, this, entityClass, fields);
	}
}
