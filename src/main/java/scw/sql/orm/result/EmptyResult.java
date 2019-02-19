package scw.sql.orm.result;

import java.util.Collections;
import java.util.Map;

import scw.common.utils.CollectionUtils;

public final class EmptyResult implements Result {
	private static final long serialVersionUID = 1L;

	public <T> T get(Class<T> type, Map<Class<?>, String> tableMapping) {
		return null;
	}

	public <T> T get(Class<T> type, String tableName) {
		return null;
	}

	public <T> T get(Class<T> type) {
		return null;
	}

	public Object[] getValues() {
		return CollectionUtils.EMPTY_ARRAY;
	}

	public <T> T get(int index) {
		return null;
	}

	public int size() {
		return 0;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getValueMap(String tableName) {
		return Collections.EMPTY_MAP;
	}

	public boolean isEmpty() {
		return true;
	}
}
