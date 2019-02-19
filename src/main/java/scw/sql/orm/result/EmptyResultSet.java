package scw.sql.orm.result;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class EmptyResultSet implements ResultSet {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public <T> List<T> getList(Class<T> type, Map<Class<?>, String> tableMapping) {
		return Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getList(Class<T> type, String tableName) {
		return Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getList(Class<T> type) {
		return Collections.EMPTY_LIST;
	}

	public int size() {
		return 0;
	}

	public Result getFirst() {
		return Result.EMPTY_RESULT;
	}

	public Result getLast() {
		return Result.EMPTY_RESULT;
	}

	public boolean isEmpty() {
		return true;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getList() {
		return Collections.EMPTY_LIST;
	}

	public Iterator<Result> iterator() {
		return Collections.emptyIterator();
	}
}
