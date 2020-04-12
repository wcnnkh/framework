package scw.orm.sql.support;

import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.orm.MappingContext;
import scw.orm.sql.AbstractSetterMapping;
import scw.orm.sql.SqlMapper;
import scw.orm.sql.TableNameMapping;
import scw.orm.sql.ValueIndexMapping;

public final class DefaultTableSetterMapping extends AbstractSetterMapping {
	private static Logger logger = LoggerUtils
			.getLogger(DefaultTableSetterMapping.class);
	private ValueIndexMapping valueIndexMapping;
	private Object[] values;
	private TableNameMapping tableNameMapping;

	public DefaultTableSetterMapping(ValueIndexMapping valueIndexMapping,
			Object[] values, TableNameMapping tableNameMapping) {
		this.valueIndexMapping = valueIndexMapping;
		this.values = values;
		this.tableNameMapping = tableNameMapping;
	}

	protected String getTableName(Class<?> clazz, SqlMapper sqlMapper) {
		String tableName = null;
		if (tableNameMapping != null) {
			tableName = tableNameMapping.getTableName(clazz);
		}

		if (StringUtils.isEmpty(tableName)) {
			tableName = sqlMapper.getTableNameMapping().getTableName(clazz);
		}
		return tableName;
	}

	protected Object getValue(MappingContext context, SqlMapper sqlMapper) {
		int index = valueIndexMapping.isExistDuplicateField() ? valueIndexMapping
				.getIndex(getTableName(context.getDeclaringClass(), sqlMapper),
						context.getColumn().getName()) : valueIndexMapping
				.getSingleIndex(context.getColumn().getName());
		if (index == -1) {
			if (!sqlMapper.isNullable(context)) {
				logger.warn(
						"{} [{}] not found for ResultSet, context-class [{}]",
						context.getColumn().getDeclaringClass().getName(),
						context.getColumn().getName(),
						context.getDeclaringClass());
			}
			return null;
		}

		Object v = values[index];
		if (v == null) {
			if (!sqlMapper.isNullable(context)) {
				logger.warn("{} [{}] not is null, context-class [{}]", context
						.getColumn().getDeclaringClass().getName(), context
						.getColumn().getName(), context.getDeclaringClass());
			}
		}
		return v;
	}
}
