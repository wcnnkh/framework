package io.basc.framework.sql.orm.convert;

import io.basc.framework.sql.SqlUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.MultiIterator;
import io.basc.framework.util.StringMatcher;
import io.basc.framework.util.StringMatchers;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.Value;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ResultSetPropertyFactory implements PropertyFactory, Serializable {
	private static final long serialVersionUID = 1L;
	private String connector = ".";
	private StringMatcher stringMatcher = StringMatchers.SIMPLE.split(connector);

	/**
	 * 多表字段对应的索引
	 */
	private Map<String, Integer> valueMap = new LinkedHashMap<String, Integer>();
	// 单表字段对应的索引
	private Map<String, Integer> singletonTableMap = new LinkedHashMap<String, Integer>();
	private Object[] values;

	public ResultSetPropertyFactory(ResultSet resultSet) throws SQLException {
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		values = new Object[columnCount];
		for (int i = 1, index = 0; i <= columnCount; i++, index++) {
			values[index] = resultSet.getObject(i);
			String name = SqlUtils.lookupColumnName(metaData, i);
			if (singletonTableMap.containsKey(name)) {
				// 如果单表索引中已经存在相同的字段名
				singletonTableMap.remove(name);

				String tableName = getTableName(metaData, i);
				String key = StringUtils.isEmpty(tableName) ? name : (tableName + connector + name);
				valueMap.put(key, index);
			} else {
				singletonTableMap.put(name, index);
			}
		}
	}

	public final StringMatcher getStringMatcher() {
		return stringMatcher;
	}

	public void setStringMatcher(StringMatcher stringMatcher) {
		Assert.requiredArgument(stringMatcher != null, "stringMatcher");
		this.stringMatcher = stringMatcher.split(connector);
	}

	public final String getConnector() {
		return connector;
	}

	public void setConnector(String connector) {
		Assert.requiredArgument(connector != null, "connector");
		this.connector = connector;
	}

	protected String getTableName(ResultSetMetaData metaData, int i) throws SQLException {
		return metaData.getTableName(i);
	}

	@Override
	public Value getValue(String key) {
		Integer index = getIndex(key);
		if (index == null) {
			return null;
		}

		return new AnyValue(values[index]);
	}

	private Integer getIndex(String key) {
		if (stringMatcher.isPattern(key)) {
			for (Entry<String, Integer> entry : singletonTableMap.entrySet()) {
				if (stringMatcher.match(key, entry.getKey())) {
					return entry.getValue();
				}
			}

			for (Entry<String, Integer> entry : valueMap.entrySet()) {
				if (stringMatcher.match(key, entry.getKey())) {
					return entry.getValue();
				}
			}
			return null;
		}

		Integer index = singletonTableMap.get(key);
		if (index == null) {
			index = valueMap.get(key);
		}
		return index;
	}

	@Override
	public boolean containsKey(String key) {
		return getIndex(key) != null;
	}

	@Override
	public Iterator<String> iterator() {
		return new MultiIterator<String>(singletonTableMap.keySet().iterator(), valueMap.keySet().iterator());
	}
}
