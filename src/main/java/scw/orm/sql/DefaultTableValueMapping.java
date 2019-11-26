package scw.orm.sql;

import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.orm.MappingContext;

public final class DefaultTableValueMapping extends AbstractValueMapping {
	private static Logger logger = LoggerUtils.getLogger(DefaultTableValueMapping.class);
	private ValueIndexMapping valueIndexMapping;
	private Object[] values;
	private TableNameMapping tableNameMapping;

	public DefaultTableValueMapping(ValueIndexMapping valueIndexMapping, Object[] values,
			TableNameMapping tableNameMapping) {
		this.valueIndexMapping = valueIndexMapping;
		this.values = values;
		this.tableNameMapping = tableNameMapping;
	}

	protected Object getValue(MappingContext context) {
		String columnName = context.getFieldDefinition().getName();
		int index = valueIndexMapping.isSingle() ? valueIndexMapping.getSingleIndexMap().get(columnName)
				: valueIndexMapping.getIndexMap(tableNameMapping.getTableName(context.getDeclaringClass()))
						.get(columnName);
		if (index == -1) {
			if (!SqlORMUtils.isNullAble(context.getFieldDefinition())) {
				logger.warn("{} [{}] not found for DataSource, context-class [{}]",
						context.getFieldDefinition().getDeclaringClass().getName(),
						context.getFieldDefinition().getName(), context.getDeclaringClass());
				return null;
			}
		}

		Object v = values[index];
		if (v == null) {
			if (!SqlORMUtils.isNullAble(context.getFieldDefinition())) {
				logger.warn("{} [{}] not is null, context-class [{}]",
						context.getFieldDefinition().getDeclaringClass().getName(),
						context.getFieldDefinition().getName(), context.getDeclaringClass());
			}
		}
		return v;
	}
}
