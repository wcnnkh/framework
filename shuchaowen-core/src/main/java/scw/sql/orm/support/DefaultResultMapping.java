package scw.sql.orm.support;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;

import scw.aop.support.FieldSetterListen;
import scw.aop.support.FieldSetterListenUtils;
import scw.core.instance.InstanceUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.sql.SqlUtils;
import scw.sql.orm.Column;
import scw.sql.orm.ObjectRelationalMapping;
import scw.sql.orm.TableNameMapping;

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

	@SuppressWarnings("unchecked")
	@Override
	protected <T> T mapping(Class<T> clazz, TableNameMapping tableNameMapping,
			ObjectRelationalMapping objectRelationalMapping) {
		String tableName = getTableName(clazz, tableNameMapping, objectRelationalMapping);
		T entity = (T) (SqlUtils.getObjectRelationalMapping().isTable(clazz)
				? FieldSetterListenUtils.getFieldSetterListenProxy(clazz).create()
				: InstanceUtils.INSTANCE_FACTORY.getInstance(clazz));
		Enumeration<Column> enumeration = objectRelationalMapping.enumeration(clazz);
		while(enumeration.hasMoreElements()){
			Column column = enumeration.nextElement();
			Object value;
			if (column.isEntity()) {
				value = get(column.getField().getSetter().getType(), tableNameMapping);
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

	@SuppressWarnings("unchecked")
	public final <T> T get(Class<? extends T> type, int index) {
		if (values == null) {
			return null;
		}

		return (T) parse(type, values[index]);
	}

	protected Object parse(Class<?> type, Object value) {
		if (value == null) {
			return value;
		}

		if (type == Object.class) {
			return value;
		}

		if (TypeUtils.isBoolean(type)) {
			if (value != null) {
				if (value instanceof Number) {
					return ((Number) value).intValue() == 1;
				} else if (value instanceof String) {
					return StringUtils.parseBoolean((String) value);
				}
			}
		} else if (TypeUtils.isInt(type)) {
			if (value instanceof Number) {
				return ((Number) value).intValue();
			}
		} else if (TypeUtils.isLong(type)) {
			if (value instanceof Number) {
				return ((Number) value).longValue();
			}
		} else if (TypeUtils.isByte(type)) {
			if (value instanceof Number) {
				return ((Number) value).byteValue();
			}
		} else if (TypeUtils.isFloat(type)) {
			if (value instanceof Number) {
				return ((Number) value).floatValue();
			}
		} else if (TypeUtils.isDouble(type)) {
			if (value instanceof Number) {
				return ((Number) value).doubleValue();
			}
		} else if (TypeUtils.isShort(type)) {
			if (value instanceof Number) {
				return ((Number) value).shortValue();
			}
		}
		return value;
	}
}
