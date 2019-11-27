package scw.orm.sql;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class EmptyResultSet implements ResultSet {
	private static final long serialVersionUID = 1L;

	public Iterator<Result> iterator() {
		return Collections.emptyIterator();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getList() {
		return Collections.EMPTY_LIST;
	}

	public <T> List<T> getList(SqlMappingOperations mappingOperations,
			Class<T> clazz, TableNameMapping tableNameMapping) {
		return null;
	}

	public <T> List<T> getList(SqlMappingOperations mappingOperations,
			Class<T> clazz, String tableName) {
		return null;
	}

	public <T> List<T> getList(SqlMappingOperations mappingOperations,
			Class<T> clazz) {
		return null;
	}

	public int size() {
		return 0;
	}

	public Result getFirst() {
		return null;
	}

	public Result getLast() {
		return null;
	}

	public boolean isEmpty() {
		return true;
	}

}
