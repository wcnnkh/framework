package scw.sql.orm.support;

import java.sql.ResultSet;
import java.sql.SQLException;

import scw.aop.support.FieldSetterListen;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mapper.Field;
import scw.sql.orm.Column;
import scw.sql.orm.ObjectRelationalMapping;
import scw.sql.orm.TableNameMapping;
import scw.value.AnyValue;
import scw.value.Value;

public class DefaultResultMapping extends AbstractResultMapping {
	private static final long serialVersionUID = 1L;
	protected static Logger logger = LoggerUtils.getLogger(DefaultResultMapping.class);

	public DefaultResultMapping(ResultSetResolver resultSetResolver, Object[] values) {
		super(resultSetResolver, values);
	}

	public DefaultResultMapping(ResultSet resultSet) throws SQLException {
		super(resultSet);
	}

	protected String getTableName(Class<?> clazz, TableNameMapping tableNameMapping,
			ObjectRelationalMapping objectRelationalMapping) {
		String tableName = null;
		if (tableNameMapping != null) {
			tableName = tableNameMapping.getTableName(clazz);
		}

		if (StringUtils.isEmpty(tableName)) {
			tableName = objectRelationalMapping.getTableName(clazz);
		}
		return tableName;
	}

	protected Object getValue(String tableName, Column column, Class<?> tableClass) {
		int index = resultSetResolver.isMultiTable() ? resultSetResolver.getIndex(tableName, column.getName())
				: resultSetResolver.getSingleIndex(column.getName());
		if (index == -1) {
			if (!column.isNullable()) {
				logger.warn("{} [{}] not found for ResultSet, context-class [{}]",
						column.getField().getSetter().getDeclaringClass().getName(), column.getName(), tableClass);
			}
			return null;
		}

		Object v = values[index];
		if (v == null) {
			if (!column.isNullable()) {
				logger.warn("{} [{}] not is null, context-class [{}]",
						column.getField().getSetter().getDeclaringClass().getName(), column.getName(), tableClass);
			}
		}
		return v;
	}

	@Override
	protected <T> T mapping(Class<T> clazz, TableNameMapping tableNameMapping,
			ObjectRelationalMapping objectRelationalMapping, Field parentField) {
		String tableName = getTableName(clazz, tableNameMapping, objectRelationalMapping);
		T entity = objectRelationalMapping.newEntity(clazz);
		for(Column column : objectRelationalMapping.getColumns(clazz, parentField)){
			Object value;
			if (column.isEntity()) {
				value = mapping(column.getField().getSetter().getType(), tableNameMapping, objectRelationalMapping,
						column.getField());
			} else {
				value = getValue(tableName, column, clazz);
			}
			column.set(entity, value);
		}

		if (entity instanceof FieldSetterListen) {
			((FieldSetterListen) entity).clear_field_setter_listen();
		}
		return entity;
	}

	public final <T> T get(Class<? extends T> type, int index) {
		if (values == null) {
			return null;
		}
		
		Value value = new AnyValue(values[index]);
		return value.getAsObject(type);
	}
}
