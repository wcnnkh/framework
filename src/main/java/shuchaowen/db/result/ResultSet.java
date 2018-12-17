package shuchaowen.db.result;

import java.io.Serializable;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import shuchaowen.common.Logger;
import shuchaowen.common.exception.NotFoundException;
import shuchaowen.common.utils.ClassUtils;
import shuchaowen.db.ColumnInfo;
import shuchaowen.db.DB;
import shuchaowen.db.TableInfo;

public final class ResultSet implements Serializable {
	private static final long serialVersionUID = -3199839587290797839L;
	private MetaData metaData;
	private ArrayList<Object[]> dataList;

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
		if (metaData == null) {// 第一次
			metaData = new MetaData(resultSet.getMetaData());
			dataList = new ArrayList<Object[]>();
		}

		Object[] values = new Object[metaData.getColumns().length];
		if (checkColumn) {
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
			for (int i = 1; i <= values.length; i++) {
				int index = metaData.getColumnIndex(resultSetMetaData.getColumnName(i),
						resultSetMetaData.getTableName(i));
				if (index == -1) {
					throw new NotFoundException(resultSetMetaData.getTableName(i) + "."
							+ resultSetMetaData.getColumnName(i) + "在原ResultSet中找不到");
				}
				values[i - 1] = resultSet.getObject(i);
			}
		} else {
			for (int i = 1; i <= values.length; i++) {
				values[i - 1] = resultSet.getObject(i);
			}
		}
		dataList.add(values);
	}

	public int size() {
		return dataList == null ? 0 : dataList.size();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getObjectList() {
		return (List<Object[]>) (dataList == null ? null : dataList.clone());
	}

	protected static Object wrapper(MetaData metaData, Object[] values, TableInfo tableInfo, String... tableName)
			throws IllegalArgumentException, IllegalAccessException {
		Object o = tableInfo.newInstance();
		for (ColumnInfo column : tableInfo.getColumns()) {
			int index;
			if (tableName == null || tableName.length == 0) {
				index = metaData.getColumnIndex(column.getName(), tableInfo.getName());
			} else {
				index = metaData.getColumnIndex(column.getName(), tableName);
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
			Object v = wrapper(metaData, values, DB.getTableInfo(column.getType()));
			if (v != null) {
				column.setValueToField(o, v);
			}
		}
		return o;
	}

	protected static Object wrapper(MetaData metaData, Object[] values, TableInfo tableInfo, TableMapping tableMapping)
			throws IllegalArgumentException, IllegalAccessException {
		Object o = tableInfo.newInstance();
		for (ColumnInfo column : tableInfo.getColumns()) {
			int index;
			if (tableMapping == null) {
				index = metaData.getColumnIndex(column.getName(), tableInfo.getName());
			} else {
				List<String> list = tableMapping.getTableNameList(tableInfo.getClassInfo().getClz());
				if (list == null) {
					index = metaData.getColumnIndex(column.getName(), tableInfo.getName());
				} else {
					index = metaData.getColumnIndex(list, column.getName());
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
			Object v = wrapper(metaData, values, DB.getTableInfo(column.getType()), tableMapping);
			if (v != null) {
				column.setValueToField(o, v);
			}
		}
		return o;
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> getTableBeanList(Class<T> type, TableMapping tableMapping)
			throws IllegalArgumentException, IllegalAccessException {
		TableInfo tableInfo = DB.getTableInfo(type);
		List<T> list = new ArrayList<T>();
		for (Object[] objs : dataList) {
			list.add((T) wrapper(metaData, objs, tableInfo, tableMapping));
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> getTableBeanList(Class<T> type, String... tableName)
			throws IllegalArgumentException, IllegalAccessException {
		TableInfo tableInfo = DB.getTableInfo(type);
		List<T> list = new ArrayList<T>();
		for (Object[] objs : dataList) {
			list.add((T) wrapper(metaData, objs, tableInfo, tableName));
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getList(Class<T> type, TableMapping tableMapping) {
		if (metaData == null) {
			return null;
		}

		if (size() == 0) {
			return null;
		}

		if (type.isArray()) {
			return (List<T>) getObjectList();
		} else if (type.getName().startsWith("java") || ClassUtils.isBasicType(type)) {
			List<Object> list = new ArrayList<Object>();
			for (Object[] objects : dataList) {
				list.add(objects[0]);
			}
			return (List<T>) list;
		} else {
			try {
				return getTableBeanList(type, tableMapping);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getList(Class<T> type, String... tableName) {
		if (metaData == null) {
			return null;
		}

		if (size() == 0) {
			return null;
		}

		if (type.isArray()) {
			return (List<T>) getObjectList();
		} else if (type.getName().startsWith("java") || ClassUtils.isBasicType(type)) {
			List<Object> list = new ArrayList<Object>();
			for (Object[] objects : dataList) {
				list.add(objects[0]);
			}
			return (List<T>) list;
		} else {
			try {
				return getTableBeanList(type, tableName);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public Object[] getObjects(int index) {
		if (index < 0 || index >= size()) {
			return null;
		}

		return dataList.get(index);
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject(Class<T> type, int index, TableMapping tableMapping) {
		if (index < 0 || index >= size()) {
			return null;
		}

		if (metaData == null) {
			return null;
		}

		if (type.isArray()) {
			Object[] src = dataList.get(index);
			Object[] dest = new Object[src.length];
			System.arraycopy(src, 0, dest, 0, dest.length);
			return (T) dest;
		} else if (type.getName().startsWith("java") || ClassUtils.isBasicType(type)) {
			Object[] values = dataList.get(index);
			if (values != null && values.length > 0) {
				return (T) values[0];
			}
			return null;
		} else {
			try {
				return (T) wrapper(metaData, dataList.get(index), DB.getTableInfo(type), tableMapping);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject(Class<T> type, int index, String... tableName) {
		if (index < 0 || index >= size()) {
			return null;
		}

		if (metaData == null) {
			return null;
		}

		if (type.isArray()) {
			Object[] src = dataList.get(index);
			Object[] dest = new Object[src.length];
			System.arraycopy(src, 0, dest, 0, dest.length);
			return (T) dest;
		} else if (type.getName().startsWith("java") || ClassUtils.isBasicType(type)) {
			Object[] values = dataList.get(index);
			if (values != null && values.length > 0) {
				return (T) values[0];
			}
			return null;
		} else {
			try {
				return (T) wrapper(metaData, dataList.get(index), DB.getTableInfo(type), tableName);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
