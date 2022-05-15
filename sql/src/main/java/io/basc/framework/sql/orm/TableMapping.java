package io.basc.framework.sql.orm;

import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.mapper.Fields;
import io.basc.framework.orm.StructureRegistry;
import io.basc.framework.orm.repository.RepositoryMapping;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

public interface TableMapping extends TableStructureProcessor,
		RepositoryMapping, TableResolver, StructureRegistry<TableStructure> {

	@Override
	default Column resolve(Class<?> entityClass, Field field) {
		return new DefaultColumn(this, entityClass, field, this);
	}

	@Override
	default ColumnMetadata resolveMetadata(Class<?> entityClass,
			FieldDescriptor fieldDescriptor) {
		return new DefaultColumnMetdata(this, entityClass, fieldDescriptor,
				this);
	}

	@Override
	default TableMetadata resolveMetadata(Class<?> entityClass) {
		return new DefaultTableMetadata(this, entityClass, this);
	}

	@Override
	default TableStructure getStructure(Class<?> entityClass) {
		return getStructure(entityClass, getFields(entityClass).all());
	}

	@Override
	default TableStructure getStructure(Class<?> entityClass, Field parentField) {
		return getStructure(entityClass, getFields(entityClass, parentField)
				.all());
	}

	@Override
	default TableStructure getStructure(Class<?> entityClass, Fields fields) {
		return new DefaultTableStructure(this, this, entityClass, fields);
	}

	default <T> TableStructure getStructure(Class<? extends T> entityClass,
			@Nullable T entity, @Nullable String tableName) {
		Assert.requiredArgument(entityClass != null, "entityClass");
		if (StringUtils.isNotEmpty(tableName)) {
			return getStructure(entityClass).rename(tableName);
		}

		TableStructure tableStructure = getStructure(entityClass);
		if (entity != null && entity instanceof TableName) {
			String entityName = ((TableName) entity).getTableName();
			if (StringUtils.isNotEmpty(entityName)) {
				tableStructure = tableStructure.rename(entityName);
			}
		}
		return tableStructure;
	}
}
