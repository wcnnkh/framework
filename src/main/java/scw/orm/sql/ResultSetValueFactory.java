package scw.orm.sql;

import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.orm.FieldDefinitionContext;
import scw.orm.ORMOperations;
import scw.orm.ValueFactory;

public class ResultSetValueFactory implements ValueFactory {
	private static Logger logger = LoggerUtils.getLogger(ResultSetValueFactory.class);
	protected MetaData metaData;
	protected Object[] values;
	private TableNameFactory tableNameFactory;

	public ResultSetValueFactory(MetaData metaData, Object[] values, TableNameFactory tableNameFactory) {
		this.metaData = metaData;
		this.values = values;
		this.tableNameFactory = tableNameFactory;
	}

	public Object getValue(FieldDefinitionContext context, ORMOperations ormOperations) {
		int index;
		if (tableNameFactory == null || metaData.isAsSingle()) {
			index = metaData.getColumnIndex(context.getFieldDefinition().getName());
		} else {
			index = metaData.getColumnIndex(context.getFieldDefinition().getName(),
					tableNameFactory.getTableName(context.getFieldDefinition().getDeclaringClass()));
		}

		if (index == -1) {
			if (!SqlORMUtils.isNullAble(context.getFieldDefinition())) {
				logger.warn("{} [{}] not found for DataSource",
						context.getFieldDefinition().getDeclaringClass().getName(),
						context.getFieldDefinition().getName());
			}
			return null;
		}

		Object v = values[index];
		if (v == null) {
			if (!SqlORMUtils.isNullAble(context.getFieldDefinition())) {
				logger.warn("{} [{}] not is null", context.getFieldDefinition().getDeclaringClass().getName(),
						context.getFieldDefinition().getName());
			}
		}
		return v;
	}

}
