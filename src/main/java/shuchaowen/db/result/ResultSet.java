package shuchaowen.db.result;

import java.io.Serializable;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shuchaowen.common.Logger;
import shuchaowen.common.exception.NotFoundException;
import shuchaowen.common.utils.ClassUtils;
import shuchaowen.db.ColumnInfo;
import shuchaowen.db.DB;
import shuchaowen.db.TableInfo;

public final class ResultSet implements Serializable {
	private static final long serialVersionUID = -3199839587290797839L;
	private Map<String, Map<String, Integer>> metaData;
	private Column[] columns;
	private List<Object[]> dataList;
	
	public ResultSet() {
	};

	public ResultSet(java.sql.ResultSet resultSet) throws SQLException {
		while (resultSet.next()) {
			append(resultSet, false);
		}
	}

	public void append(java.sql.ResultSet resultSet) throws SQLException {
		append(resultSet, true);
	}

	private void append(java.sql.ResultSet resultSet, boolean checkColumn) throws SQLException {
		if (resultSet == null) {
			return;
		}
		if (columns == null) {// 第一次
			if (metaData == null) {
				metaData = new HashMap<String, Map<String, Integer>>(4);
			}

			if (dataList == null) {
				dataList = new ArrayList<Object[]>();
			}

			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
			columns = new Column[resultSetMetaData.getColumnCount()];
			Object[] values = new Object[columns.length];
			for (int i = 0; i < columns.length; i++) {
				Column column = new Column(resultSetMetaData.getColumnName(i), resultSetMetaData.getTableName(i));
				values[i] = resultSet.getObject(i);
				columns[i] = column;
				Map<String, Integer> map = metaData.get(column.getTableName());
				if (map == null) {
					map = new HashMap<String, Integer>();
					map.put(column.getName(), i);
					metaData.put(column.getTableName(), map);
				} else {
					map.put(column.getName(), i);
				}
			}
			dataList.add(values);
		} else {
			Object[] values = new Object[columns.length];
			if (checkColumn) {
				ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
				for (int i = 0; i < columns.length; i++) {
					Map<String, Integer> map = metaData.get(resultSetMetaData.getTableName(i));
					if (metaData == null) {
						throw new NotFoundException(resultSetMetaData.getTableName(i) + "表在原ResultSet中找不到");
					}

					if (!map.containsKey(resultSetMetaData.getColumnName(i))) {
						throw new NotFoundException(resultSetMetaData.getColumnName(i) + "字段在原ResultSet中找不到");
					}

					values[i] = resultSet.getObject(i);
				}
			} else {
				for (int i = 0; i < columns.length; i++) {
					values[i] = resultSet.getObject(i);
				}
			}
			dataList.add(values);
		}
	}

	public int size() {
		return dataList == null ? 0 : dataList.size();
	}

	public List<Object[]> getObjectList() {
		return dataList;
	}

	/**
	 * @param tableNameList
	 * @param columnName
	 * @return 不存在返回-1
	 */
	private int getColumnIndex(List<String> tableNameList, String columnName) {
		for (String name : tableNameList) {
			Map<String, Integer> map = metaData.get(name);
			if (map == null) {
				continue;
			}

			Integer index = map.get(columnName);
			return index == null ? -1 : index;
		}
		return -1;
	}

	private int getColumnIndex(String columnName, String... tableNames) {
		for (String name : tableNames) {
			Map<String, Integer> map = metaData.get(name);
			if (map == null) {
				continue;
			}

			Integer index = map.get(columnName);
			return index == null ? -1 : index;
		}
		return -1;
	}

	private Object wrapper(Object[] values, TableInfo tableInfo, String... tableName)
			throws IllegalArgumentException, IllegalAccessException {
		Object o = tableInfo.getClassInfo().getBeanListenInterfaces();
		for (ColumnInfo column : tableInfo.getColumns()) {
			int index;
			if (tableName == null || tableName.length == 0) {
				index = getColumnIndex(column.getName(), tableInfo.getName());
			} else {
				index = getColumnIndex(column.getName(), tableName);
			}

			if (index == -1) {
				StringBuilder sb = new StringBuilder();
				sb.append(tableInfo.getClassInfo().getName());
				sb.append(" [");
				sb.append(column.getName());
				sb.append("] not found for DataSource");
				Logger.warn("Result", sb.toString());
			}

			Object v = values[index];
			if (v != null) {
				column.setValueToField(o, v);
			}
		}

		for (ColumnInfo column : tableInfo.getTableColumns()) {
			Object v = wrapper(values, DB.getTableInfo(column.getType()));
			if (v != null) {
				column.setValueToField(o, v);
			}
		}
		return o;
	}

	private Object wrapper(Object[] values, TableInfo tableInfo, TableMapping tableMapping)
			throws IllegalArgumentException, IllegalAccessException {
		Object o = tableInfo.getClassInfo().getBeanListenInterfaces();
		for (ColumnInfo column : tableInfo.getColumns()) {
			int index;
			if (tableMapping == null) {
				index = getColumnIndex(column.getName(), tableInfo.getName());
			} else {
				List<String> list = tableMapping.getTableNameList(tableInfo.getClassInfo().getClz());
				if (list == null) {
					index = getColumnIndex(column.getName(), tableInfo.getName());
				} else {
					index = getColumnIndex(list, column.getName());
				}
			}

			if (index == -1) {
				StringBuilder sb = new StringBuilder();
				sb.append(tableInfo.getClassInfo().getName());
				sb.append(" [");
				sb.append(column.getName());
				sb.append("] not found for DataSource");
				Logger.warn("Result", sb.toString());
			}

			Object v = values[index];
			if (v != null) {
				column.setValueToField(o, v);
			}
		}

		for (ColumnInfo column : tableInfo.getTableColumns()) {
			Object v = wrapper(values, DB.getTableInfo(column.getType()), tableMapping);
			if (v != null) {
				column.setValueToField(o, v);
			}
		}
		return o;
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> getTableBeanList(Class<T> type, TableMapping tableMapping)
			throws IllegalArgumentException, IllegalAccessException {
		if (metaData == null) {
			return null;
		}

		if (size() == 0) {
			return null;
		}

		TableInfo tableInfo = DB.getTableInfo(type);
		List<T> list = new ArrayList<T>();
		for (Object[] objs : dataList) {
			list.add((T) wrapper(objs, tableInfo, tableMapping));
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> getTableBeanList(Class<T> type, String... tableName)
			throws IllegalArgumentException, IllegalAccessException {
		if (metaData == null) {
			return null;
		}

		if (size() == 0) {
			return null;
		}

		TableInfo tableInfo = DB.getTableInfo(type);
		List<T> list = new ArrayList<T>();
		for (Object[] objs : dataList) {
			list.add((T) wrapper(objs, tableInfo, tableName));
		}
		return list;
	}

	public <T> List<T> getList(Class<T> type, TableMapping tableMapping) {
		try {
			return getTableBeanList(type, tableMapping);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public <T> List<T> getList(Class<T> type, String... tableName) {
		try {
			return getTableBeanList(type, tableName);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object[] getObjects(int index) {
		if (index < 0 || index >= size()) {
			return null;
		}

		return dataList.get(index);
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject(int index, Class<T> type, TableMapping tableMapping) {
		if (index < 0 || index >= size()) {
			return null;
		}
		
		if (metaData == null) {
			return null;
		}

		if (type.isArray()) {
			return (T) dataList.get(index);
		} else if (type.getName().startsWith("java") || ClassUtils.isBasicType(type)) {
			Object[] values = dataList.get(index);
			if (values != null && values.length > 0) {
				return (T) values[0];
			}
			return null;
		} else {
			try {
				return (T) wrapper(dataList.get(index), DB.getTableInfo(type), tableMapping);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject(int index, Class<T> type, String... tableName) {
		if (index < 0 || index >= size()) {
			return null;
		}
		
		if (metaData == null) {
			return null;
		}

		if (type.isArray()) {
			return (T) dataList.get(index);
		} else if (type.getName().startsWith("java") || ClassUtils.isBasicType(type)) {
			Object[] values = dataList.get(index);
			if (values != null && values.length > 0) {
				return (T) values[0];
			}
			return null;
		} else {
			try {
				return (T) wrapper(dataList.get(index), DB.getTableInfo(type), tableName);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
