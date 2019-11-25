package scw.orm.sql;

import java.util.Collections;
import java.util.Map;

import scw.orm.ORMOperations;

public final class EmptyResult implements Result {
	private static final long serialVersionUID = 1L;

	public Object[] getValues() {
		return new Object[0];
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

	@Override
	public Object clone() {
		return EMPTY_RESULT;
	}

	public <T> T get(ORMOperations ormOperations, Class<T> clazz, TableNameFactory tableNameFactory) {
		return null;
	}

	public <T> T get(ORMOperations ormOperations, Class<T> clazz) {
		return null;
	}
}
