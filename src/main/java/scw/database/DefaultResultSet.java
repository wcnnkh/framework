package scw.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class DefaultResultSet implements ResultSet {
	private static final long serialVersionUID = 1L;
	private MetaData metaData;
	private LinkedList<Object[]> dataList;

	/**
	 * 序列化用的
	 */
	protected DefaultResultSet() {
	};

	public DefaultResultSet(java.sql.ResultSet resultSet) throws SQLException {
		while (resultSet.next()) {
			if (metaData == null) {// 第一次
				metaData = new MetaData(resultSet.getMetaData());
				dataList = new LinkedList<Object[]>();
			}

			Object[] values = new Object[metaData.getColumns().length];
			for (int i = 1; i <= values.length; i++) {
				values[i - 1] = resultSet.getObject(i);
			}
			dataList.add(values);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> wrapper(Class<T> type, TableInfo tableInfo, String tableName,
			Map<Class<?>, String> tableMapping) throws IllegalArgumentException, IllegalAccessException {
		String tName = DefaultResult.getTableName(tableInfo, tableName, type, tableMapping);
		List<T> list = new ArrayList<T>(dataList.size());
		for (Object[] values : dataList) {
			DefaultResult defaultResult = new DefaultResult(metaData, values);
			list.add((T) defaultResult.wrapper(tableInfo, tName, tableMapping));
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> getListByFirst(Class<T> type) {
		List<Object> list = new ArrayList<Object>(dataList.size());
		for (Object[] objects : dataList) {
			list.add(objects[0]);
		}
		return (List<T>) list;
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> getAllList(Class<T> type) {
		return new ArrayList<T>((Collection<T>) dataList);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getList(Class<T> type, Map<Class<?>, String> tableMapping) {
		if (isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		if (type.isArray()) {
			return getAllList(type);
		} else if (DefaultResult.isOriginalType(type)) {
			return getListByFirst(type);
		}

		TableInfo tableInfo = DataBaseUtils.getTableInfo(type);
		try {
			return wrapper(type, tableInfo, tableInfo.getName(), tableMapping);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getList(Class<T> type, String tableName) {
		if (isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		if (type.isArray()) {
			return getAllList(type);
		} else if (DefaultResult.isOriginalType(type)) {
			return getListByFirst(type);
		}

		TableInfo tableInfo = DataBaseUtils.getTableInfo(type);
		try {
			return wrapper(type, tableInfo, tableName, null);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getList(Class<T> type) {
		if (isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		if (type.isArray()) {
			return getAllList(type);
		} else if (DefaultResult.isOriginalType(type)) {
			return getListByFirst(type);
		}

		TableInfo tableInfo = DataBaseUtils.getTableInfo(type);
		try {
			return wrapper(type, tableInfo, tableInfo.getName(), null);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return Collections.EMPTY_LIST;
	}

	public int size() {
		return dataList == null ? 0 : dataList.size();
	}

	public Result getFirst() {
		return new DefaultResult(metaData, dataList.getFirst());
	}

	public Result getLast() {
		return new DefaultResult(metaData, dataList.getLast());
	}

	public boolean isEmpty() {
		return metaData == null || metaData.isEmpty() || dataList == null;
	}

	public Iterator<Result> iterator() {
		if (isEmpty()) {
			return Collections.emptyIterator();
		}

		return new ResultIterator();
	}

	final class ResultIterator implements Iterator<Result> {
		private Iterator<Object[]> iterator;

		public ResultIterator() {
			iterator = dataList.iterator();
		}

		public boolean hasNext() {
			return iterator.hasNext();
		}

		public Result next() {
			return new DefaultResult(metaData, iterator.next());
		}
	}

	public List<Object[]> getList() {
		return new ArrayList<Object[]>(dataList);
	}
}
