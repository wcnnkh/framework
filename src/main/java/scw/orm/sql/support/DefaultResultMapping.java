package scw.orm.sql.support;

import java.sql.ResultSet;
import java.sql.SQLException;

import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.orm.sql.AbstractResultMapping;
import scw.orm.sql.DefaultTableSetterMapping;
import scw.orm.sql.TableNameMapping;
import scw.orm.sql.ValueIndexMapping;

public class DefaultResultMapping extends AbstractResultMapping {
	private static final long serialVersionUID = 1L;

	public DefaultResultMapping(ValueIndexMapping valueIndexMapping, Object[] values) {
		super(valueIndexMapping, values);
	}

	public DefaultResultMapping(ResultSet resultSet) throws SQLException {
		super(resultSet);
	}

	@Override
	protected <T> T mapping(Class<T> clazz, TableNameMapping tableNameMapping) {
		return SqlORMUtils.getSqlMapper().create(null, clazz,
				new DefaultTableSetterMapping(valueIndexMapping, values, tableNameMapping));
	}

	@SuppressWarnings("unchecked")
	public final <T> T get(Class<T> type, int index) {
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
