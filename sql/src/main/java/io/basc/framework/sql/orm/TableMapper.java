package io.basc.framework.sql.orm;

import java.sql.ResultSet;
import java.sql.SQLException;

import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.StructureFactory;
import io.basc.framework.orm.ObjectMapper;
import io.basc.framework.orm.repository.RepositoryMapper;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

public interface TableMapper extends RepositoryMapper, TableResolver, StructureFactory<TableStructure>,
		ObjectMapper<ResultSet, SQLException> {

	@Override
	default TableStructure getStructure(Class<?> entityClass) {
		return getStructure(entityClass, this, null).withSuperclass().clone();
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
