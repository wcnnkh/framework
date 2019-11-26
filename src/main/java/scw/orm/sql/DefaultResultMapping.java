package scw.orm.sql;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.orm.MappingOperations;
import scw.orm.ORMException;

public class DefaultResultMapping implements Result {
	private static final long serialVersionUID = 1L;
	protected final ValueIndexMapping valueIndexMapping;
	protected final Object[] values;

	public DefaultResultMapping(ValueIndexMapping valueIndexMapping, Object[] values) {
		this.valueIndexMapping = valueIndexMapping;
		this.values = values;
	}

	public final ValueIndexMapping getValueIndexMapping() {
		return valueIndexMapping;
	}

	public <T> T get(MappingOperations mappingOperations, Class<T> clazz, TableNameMapping tableNameMapping) {
		try {
			return mappingOperations.create(null, clazz,
					new DefaultTableValueMapping(valueIndexMapping, values, tableNameMapping));
		} catch (Exception e) {
			throw new ORMException(clazz.getName(), e);
		}
	}

	public <T> T get(SqlMappingOperations mappingOperations, Class<T> clazz, String tableName) {
		return get(mappingOperations, clazz, new SingleTableNameMapping(clazz, tableName, mappingOperations));
	}

	public <T> T get(SqlMappingOperations mappingOperations, Class<T> clazz) {
		return get(mappingOperations, clazz, mappingOperations);
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
	public final <T> T get(Class<T> type, int index) {
		if (values == null) {
			return null;
		}

		return (T) parse(type, values[index]);
	}

	protected Object parse(Class<?> type, Object value) {
		if (value == null) {
			return value;
		}

		if (TypeUtils.isBoolean(type)) {
			if (value != null) {
				if (value instanceof Number) {
					return ((Number) value).intValue() == 1;
				} else if (value instanceof String) {
					return StringUtils.parseBoolean((String) value);
				}
			}
		} else if (TypeUtils.isInt(type)) {
			if (value instanceof Number) {
				return ((Number) value).intValue();
			}
		} else if (TypeUtils.isLong(type)) {
			if (value instanceof Number) {
				return ((Number) value).longValue();
			}
		} else if (TypeUtils.isByte(type)) {
			if (value instanceof Number) {
				return ((Number) value).byteValue();
			}
		} else if (TypeUtils.isFloat(type)) {
			if (value instanceof Number) {
				return ((Number) value).floatValue();
			}
		} else if (TypeUtils.isDouble(type)) {
			if (value instanceof Number) {
				return ((Number) value).doubleValue();
			}
		} else if (TypeUtils.isShort(type)) {
			if (value instanceof Number) {
				return ((Number) value).shortValue();
			}
		}
		return value;
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
