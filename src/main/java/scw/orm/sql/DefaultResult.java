package scw.orm.sql;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.orm.ORMException;
import scw.orm.ORMOperations;

public class DefaultResult implements Result {
	private static final long serialVersionUID = 1L;
	protected MetaData metaData;
	protected Object[] values;

	public DefaultResult(MetaData metaData, Object[] values) {
		this.metaData = metaData;
		this.values = values;
	}

	@Override
	public Object clone() {
		return new DefaultResult(metaData, values);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getValueMap(String tableName) {
		if (isEmpty()) {
			return Collections.EMPTY_MAP;
		}

		Map<String, Object> valueMap = new LinkedHashMap<String, Object>();
		MetaDataColumn[] columns = metaData.getColumns();
		for (int i = 0; i < columns.length; i++) {
			valueMap.put(columns[i].getName(), values[i]);
		}
		return valueMap;
	}

	public <T> T get(ORMOperations ormOperations, Class<T> clazz, TableNameFactory tableNameFactory) {
		try {
			return ormOperations.create(null, clazz, new ResultSetValueFactory(metaData, values, tableNameFactory));
		} catch (Exception e) {
			throw new ORMException(clazz.getName(), e);
		}
	}

	public <T> T get(ORMOperations ormOperations, Class<T> clazz) {
		return get(ormOperations, clazz, null);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(int index) {
		if (values == null) {
			return null;
		}

		return (T) values[index];
	}

	public int size() {
		return values == null ? 0 : values.length;
	}

	public Object[] getValues() {
		if (values == null) {
			return null;
		}

		Object[] dest = new Object[values.length];
		System.arraycopy(values, 0, dest, 0, dest.length);
		return dest;
	}

	public boolean isEmpty() {
		return values == null || values.length == 0 || metaData == null || metaData.isEmpty();
	}

}
