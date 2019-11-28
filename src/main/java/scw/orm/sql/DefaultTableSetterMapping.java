package scw.orm.sql;

import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.orm.MappingContext;

public final class DefaultTableSetterMapping extends AbstractSetterMapping {
	private static Logger logger = LoggerUtils.getLogger(DefaultTableSetterMapping.class);
	private ValueIndexMapping valueIndexMapping;
	private Object[] values;
	private TableNameMapping tableNameMapping;

	public DefaultTableSetterMapping(ValueIndexMapping valueIndexMapping, Object[] values,
			TableNameMapping tableNameMapping) {
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
			tableName = sqlMapper.getTableName(clazz);
		}
		return tableName;
	}

	protected Object getValue(MappingContext context, SqlMapper sqlMapper) {
		String columnName = context.getFieldDefinition().getName();
		int index = valueIndexMapping.isSingle() ? valueIndexMapping.getSingleIndexMap().get(columnName)
				: valueIndexMapping.getIndex(getTableName(context.getDeclaringClass(), sqlMapper),
						columnName);
		if (index == -1) {
			if (!sqlMapper.isNullAble(context)) {
				logger.warn("{} [{}] not found for ResultSet, context-class [{}]",
						context.getFieldDefinition().getDeclaringClass().getName(),
						context.getFieldDefinition().getName(), context.getDeclaringClass());
			}
			return null;
		}

		Object v = values[index];
		if (v == null) {
			if (!sqlMapper.isNullAble(context)) {
				logger.warn("{} [{}] not is null, context-class [{}]",
						context.getFieldDefinition().getDeclaringClass().getName(),
						context.getFieldDefinition().getName(), context.getDeclaringClass());
			}
		}
		return v;
	}
}
