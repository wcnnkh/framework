package scw.orm.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import scw.sql.SqlUtils;

public abstract class AbstractResultSet implements ResultSet {
	private static final long serialVersionUID = 1L;
	protected ValueIndexMapping valueIndexMapping;
	protected LinkedList<Object[]> dataList;

	public AbstractResultSet(ValueIndexMapping valueIndexMapping, LinkedList<Object[]> dataList) {
		this.valueIndexMapping = valueIndexMapping;
		this.dataList = dataList;
	}

	public AbstractResultSet(java.sql.ResultSet resultSet) throws SQLException {
		while (resultSet.next()) {
			if (valueIndexMapping == null) {// 第一次
				valueIndexMapping = new ResultSetValueIndexMapping(resultSet.getMetaData());
				dataList = new LinkedList<Object[]>();
			}

			dataList.add(SqlUtils.getRowValues(resultSet, valueIndexMapping.getColumnCount()));
		}
	}

	public final ValueIndexMapping getValueIndexMapping() {
		return valueIndexMapping;
	}

	public final LinkedList<Object[]> getDataList() {
		return dataList;
	}

	@SuppressWarnings("unchecked")
	public final List<Object[]> getList() {
		if (isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		return new ArrayList<Object[]>(dataList);
	}

	public <T> List<T> getList(Class<T> clazz, TableNameMapping tableNameMapping) {
		if (isEmpty()) {
			return null;
		}

		List<T> list = new ArrayList<T>(dataList.size());
		for (Object[] values : dataList) {
			ResultMapping resultMapping = createResult(values);
			list.add(resultMapping.get(clazz, tableNameMapping));
		}
		return list;
	}

	public <T> List<T> getList(Class<T> clazz, String tableName) {
		return getList(clazz, new SingleTableNameMapping(clazz, tableName));
	}

	public <T> List<T> getList(Class<T> clazz) {
		return getList(clazz, (String) null);
	}

	protected abstract ResultMapping createResult(Object[] values);

	public final int size() {
		return dataList == null ? 0 : dataList.size();
	}

	public final ResultMapping getFirst() {
		if (isEmpty()) {
			return ResultMapping.EMPTY_RESULT;
		}

		return createResult(dataList.getFirst());
	}

	public final ResultMapping getLast() {
		if (isEmpty()) {
			return ResultMapping.EMPTY_RESULT;
		}

		return createResult(dataList.getLast());
	}

	public final boolean isEmpty() {
		return valueIndexMapping == null || dataList == null || dataList.isEmpty();
	}

	public final Iterator<ResultMapping> iterator() {
		return new ResultIterator();
	}

	final class ResultIterator implements Iterator<ResultMapping> {
		private Iterator<Object[]> iterator;

		public ResultIterator() {
			iterator = dataList.iterator();
		}

		public boolean hasNext() {
			return iterator.hasNext();
		}

		public ResultMapping next() {
			return createResult(iterator.next());
		}
	}
}
