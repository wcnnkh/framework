package scw.orm.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import scw.sql.SqlUtils;

public class DefaultResultSetMapping implements ResultSet {
	private static final long serialVersionUID = 1L;
	protected ValueIndexMapping valueIndexMapping;
	protected LinkedList<Object[]> dataList;

	public DefaultResultSetMapping(ValueIndexMapping valueIndexMapping, LinkedList<Object[]> dataList) {
		this.valueIndexMapping = valueIndexMapping;
		this.dataList = dataList;
	}

	public DefaultResultSetMapping(java.sql.ResultSet resultSet) throws SQLException {
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

	public <T> List<T> getList(SqlMappingOperations mappingOperations, Class<T> clazz,
			TableNameMapping tableNameMapping) {
		if (isEmpty()) {
			return null;
		}

		List<T> list = new ArrayList<T>(dataList.size());
		for (Object[] values : dataList) {
			Result result = createResult(values);
			list.add(result.get(mappingOperations, clazz, tableNameMapping));
		}
		return list;
	}

	public <T> List<T> getList(SqlMappingOperations mappingOperations, Class<T> clazz, String tableName) {
		if (isEmpty()) {
			return null;
		}

		TableNameMapping tableNameMapping = new SingleTableNameMapping(clazz, tableName);
		List<T> list = new ArrayList<T>(dataList.size());
		for (Object[] values : dataList) {
			Result result = createResult(values);
			list.add(result.get(mappingOperations, clazz, tableNameMapping));
		}
		return list;
	}

	public <T> List<T> getList(SqlMappingOperations mappingOperations, Class<T> clazz) {
		if (dataList == null) {
			return null;
		}

		List<T> list = new ArrayList<T>(dataList.size());
		for (Object[] values : dataList) {
			Result result = createResult(values);
			list.add(result.get(mappingOperations, clazz));
		}
		return list;
	}

	protected Result createResult(Object[] values) {
		return new DefaultResultMapping(valueIndexMapping, values);
	}

	public final int size() {
		return dataList == null ? 0 : dataList.size();
	}

	public final Result getFirst() {
		if (isEmpty()) {
			return Result.EMPTY_RESULT;
		}

		return createResult(dataList.getFirst());
	}

	public final Result getLast() {
		if (isEmpty()) {
			return Result.EMPTY_RESULT;
		}

		return createResult(dataList.getLast());
	}

	public final boolean isEmpty() {
		return valueIndexMapping == null || dataList == null || dataList.isEmpty();
	}

	public final Iterator<Result> iterator() {
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
			return createResult(iterator.next());
		}
	}
}
