package io.basc.framework.sql.orm;

import java.sql.ResultSet;
import java.sql.SQLException;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.orm.repository.RepositoryMapper;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

public interface TableMapper extends RepositoryMapper, TableResolver, ObjectMapper<ResultSet, SQLException> {

	@Override
	default Boolean isEntity(Class<?> entityClass) {
		return RepositoryMapper.super.isEntity(entityClass);
	}

	@Override
	default Boolean isEntity(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ObjectMapper.super.isEntity(entityClass, descriptor);
	}

	@Override
	default TableStructure getStructure(Class<?> entityClass) {
		return new TableStructure(entityClass, this, null).withSuperclass().clone();
	}

	default <T> TableStructure getStructure(Class<? extends T> entityClass, @Nullable T entity,
			@Nullable String tableName) {
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
