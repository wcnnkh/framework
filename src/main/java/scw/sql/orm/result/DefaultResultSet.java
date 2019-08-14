package scw.sql.orm.result;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

public final class DefaultResultSet extends AbstractResultSet {
	private static final long serialVersionUID = 1L;

	/**
	 * 序列化用的
	 */
	protected DefaultResultSet() {
	};

	DefaultResultSet(MetaData metaData, LinkedList<Object[]> dataList) {
		super(metaData, dataList);
	}

	public DefaultResultSet(java.sql.ResultSet resultSet) throws SQLException {
		super(resultSet);
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> wrapper(Class<T> type, TableInfo tableInfo, String tableName,
			Map<Class<?>, String> tableMapping) throws Exception {
		String tName;
		if (!metaData.isAsSingle()) {
			tName = tableName;
		} else {
			tName = DefaultResult.getTableName(tableInfo, tableName, type, tableMapping);
		}

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
			list.add((T) ORMUtils.parse(type, objects[0]));
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

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		try {
			return wrapper(type, tableInfo, tableInfo.getDefaultName(), tableMapping);
		} catch (Exception e) {
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

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		try {
			return wrapper(type, tableInfo, tableName, null);
		} catch (Exception e) {
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

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		try {
			return wrapper(type, tableInfo, tableInfo.getDefaultName(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.EMPTY_LIST;
	}

	public Result getFirst() {
		if (isEmpty()) {
			return Result.EMPTY_RESULT;
		}

		return new DefaultResult(metaData, dataList.getFirst());
	}

	public Result getLast() {
		if (isEmpty()) {
			return Result.EMPTY_RESULT;
		}

		return new DefaultResult(metaData, dataList.getLast());
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

	@Override
	public Object clone() {
		return new DefaultResultSet(metaData, dataList);
	}
}
