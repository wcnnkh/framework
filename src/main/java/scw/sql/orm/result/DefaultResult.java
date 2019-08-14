package scw.sql.orm.result;

import java.sql.SQLException;
import java.util.Map;

import scw.core.utils.ClassUtils;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

public final class DefaultResult extends AbstractResult {
	private static final long serialVersionUID = 1L;

	/**
	 * 序列化用
	 */
	protected DefaultResult() {
	}

	public DefaultResult(MetaData metaData, Object[] values) {
		super(metaData, values);
	}

	public DefaultResult(java.sql.ResultSet resultSet) throws SQLException {
		super(resultSet);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> type, Map<Class<?>, String> tableMapping) {
		if (isEmpty()) {
			return null;
		}

		if (type.isArray()) {
			return (T) getValues();
		} else if (isOriginalType(type)) {
			if (values.length > 0) {
				return (T) values[0];
			}
			return null;
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		String tableName = getTableName(tableInfo, tableInfo.getDefaultName(), type, tableMapping);
		try {
			return (T) wrapper(tableInfo, tableName, tableMapping);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> type, String tableName) {
		if (isEmpty()) {
			return null;
		}

		if (type.isArray()) {
			return (T) getValues();
		} else if (isOriginalType(type)) {
			if (values.length > 0) {
				return (T) values[0];
			}
			return null;
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		String tName = getTableName(tableInfo, tableName, type, null);
		try {
			return (T) wrapper(tableInfo, tName, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> type) {
		if (isEmpty()) {
			return null;
		}

		if (type.isArray()) {
			return (T) getValues();
		} else if (isOriginalType(type)) {
			if (values.length > 0) {
				return (T) ORMUtils.parse(type, values[0]);
			}
			return null;
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		String tableName = getTableName(tableInfo, tableInfo.getDefaultName(), type, null);
		try {
			return (T) wrapper(tableInfo, tableName, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected static boolean isOriginalType(Class<?> type) {
		return type.getName().startsWith("java.") || ClassUtils.isPrimitiveOrWrapper(type);
	}

	@Override
	public Object clone() {
		return new DefaultResult(metaData, values);
	}
	
}
