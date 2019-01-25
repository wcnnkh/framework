package scw.database;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.utils.ClassUtils;

public final class DefaultResult implements Result {
	private static final long serialVersionUID = 1L;
	private MetaData metaData;
	private Object[] values;

	/**
	 * 序列化用
	 */
	protected DefaultResult() {
	}

	public DefaultResult(MetaData metaData, Object[] values) {
		this.metaData = metaData;
		this.values = values;
	}

	public DefaultResult(java.sql.ResultSet resultSet) throws SQLException {
		metaData = new MetaData(resultSet.getMetaData());
		values = new Object[metaData.getColumns().length];
		for (int i = 0; i < values.length; i++) {
			values[i] = resultSet.getObject(i + 1);
		}
	}

	private Object wrapper(TableInfo tableInfo, String tableName)
			throws IllegalArgumentException, IllegalAccessException {
		Object o = tableInfo.newInstance();
		for (ColumnInfo column : tableInfo.getColumns()) {
			int index = metaData.getColumnIndex(column.getName(), tableName);
			if (index == -1 && !column.isNullAble()) {
				StringBuilder sb = new StringBuilder();
				sb.append(tableInfo.getClassInfo().getName());
				sb.append(" [");
				sb.append(column.getName());
				sb.append("] not found for DataSource");
				throw new ShuChaoWenRuntimeException(sb.toString());
			}

			Object v = values[index];
			if (v != null) {
				column.setValueToField(o, v);
			}
		}
		return o;
	}

	protected static String getTableName(TableInfo tableInfo, String tableName, Class<?> type,
			Map<Class<?>, String> tableMapping) {
		if (tableInfo.isTable()) {
			if (tableMapping == null) {
				return tableName;
			} else {
				String name = tableMapping.get(type);
				if (name == null) {
					return tableName;
				} else {
					return name;
				}
			}
		} else {
			if (tableMapping == null) {
				return "";
			} else {
				String name = tableMapping.get(type);
				if (name == null) {
					return tableName;
				} else {
					return "";
				}
			}
		}
	}

	protected Object wrapper(TableInfo tableInfo, String tableName, Map<Class<?>, String> tableMapping)
			throws IllegalArgumentException, IllegalAccessException {
		Object o = wrapper(tableInfo, tableName);
		for (ColumnInfo column : tableInfo.getTableColumns()) {
			TableInfo tInfo = DataBaseUtils.getTableInfo(column.getType());
			String tName = getTableName(tInfo, tInfo.getName(), column.getType(), tableMapping);
			Object v = wrapper(tInfo, tName, tableMapping);
			if (v != null) {
				column.setValueToField(o, v);
			}
		}
		return o;
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

		TableInfo tableInfo = DataBaseUtils.getTableInfo(type);
		String tableName = getTableName(tableInfo, tableInfo.getName(), type, tableMapping);
		try {
			return (T) wrapper(tableInfo, tableName, tableMapping);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
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

		TableInfo tableInfo = DataBaseUtils.getTableInfo(type);
		String tName = getTableName(tableInfo, tableName, type, null);
		try {
			return (T) wrapper(tableInfo, tName, null);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
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
				return (T) values[0];
			}
			return null;
		}

		TableInfo tableInfo = DataBaseUtils.getTableInfo(type);
		String tableName = getTableName(tableInfo, tableInfo.getName(), type, null);
		try {
			return (T) wrapper(tableInfo, tableName, null);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object[] getValues() {
		if (values == null) {
			return null;
		}

		Object[] dest = new Object[values.length];
		System.arraycopy(values, 0, dest, 0, dest.length);
		return dest;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(int index) {
		if (values == null) {
			return null;
		}

		return (T) values[index];
	}

	public int size() {
		return values == null ? 0 : values.length;
	}

	public boolean isEmpty() {
		return values == null || values.length == 0 || metaData == null || metaData.isEmpty();
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getValueMap(String tableName) {
		if (isEmpty()) {
			return Collections.EMPTY_MAP;
		}

		Map<String, Object> valueMap = new LinkedHashMap<String, Object>();
		MetaDataColumn[] columns = metaData.getColumns();
		for (int i = 0; i < columns.length; i++) {
			valueMap.put(columns[i].getName(), values[i]);
		}
		return valueMap;
	}

	protected static boolean isOriginalType(Class<?> type) {
		return type.getName().startsWith("java.") || ClassUtils.isPrimitiveOrWrapper(type);
	}
}
