package scw.orm.sql;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.FieldSetterListen;
import scw.core.utils.TypeUtils;
import scw.orm.sql.support.ResultSetValueIndexMapping;
import scw.sql.SqlUtils;

public abstract class AbstractResultMapping implements ResultMapping {
	private static final long serialVersionUID = 1L;
	protected final ValueIndexMapping valueIndexMapping;
	protected final Object[] values;

	public AbstractResultMapping(ValueIndexMapping valueIndexMapping, Object[] values) {
		this.valueIndexMapping = valueIndexMapping;
		this.values = values;
	}

	public AbstractResultMapping(ResultSet resultSet) throws SQLException {
		this.valueIndexMapping = new ResultSetValueIndexMapping(resultSet.getMetaData());
		this.values = SqlUtils.getRowValues(resultSet, valueIndexMapping.getColumnCount());
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> clazz, TableNameMapping tableNameMapping) {
		if (isEmpty()) {
			return null;
		}

		if (TypeUtils.isPrimitiveOrWrapper(clazz)) {
			return get(clazz, 0);
		}

		if (clazz.isArray()) {
			Object array = Array.newInstance(clazz.getComponentType(), size());
			for (int i = 0; i < size(); i++) {
				Array.set(array, i, get(clazz.getComponentType(), i));
			}
			return (T) array;
		}

		T v = mapping(clazz, tableNameMapping);
		if (v instanceof FieldSetterListen) {
			((FieldSetterListen) v).clear_field_setter_listen();
		}
		return v;
	}

	protected abstract <T> T mapping(Class<T> clazz, TableNameMapping tableNameMapping);

	public final <T> T get(Class<T> clazz, String tableName) {
		return get(clazz, new SingleTableNameMapping(clazz, tableName));
	}

	public final <T> T get(Class<T> clazz) {
		return get(clazz, (String) null);
	}

	public final Map<String, Object> getTableValueMap(String tableName) {
		Map<String, Integer> indexMap = valueIndexMapping.getIndexMap(tableName);
		if (indexMap == null) {
			return null;
		}

		Map<String, Object> valueMap = new LinkedHashMap<String, Object>(indexMap.size());
		for (Entry<String, Integer> entry : indexMap.entrySet()) {
			valueMap.put(entry.getKey(), values[entry.getValue()]);
		}
		return valueMap;
	}

	public final Object[] getValues() {
		if (values == null) {
			return null;
		}

		Object[] dest = new Object[values.length];
		System.arraycopy(values, 0, dest, 0, dest.length);
		return dest;
	}

	@SuppressWarnings("unchecked")
	public final <T> T get(int index) {
		if (values == null) {
			return null;
		}

		return (T) values[index];
	}

	public final int size() {
		return values == null ? 0 : values.length;
	}

	public final boolean isEmpty() {
		return valueIndexMapping == null || values == null || values.length == 0;
	}
}
