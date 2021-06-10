package scw.orm.sql.convert;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.core.utils.StringUtils;
import scw.sql.SqlUtils;
import scw.value.AnyValue;
import scw.value.PropertyFactory;
import scw.value.Value;

public class ResultSetPropertyFactory implements PropertyFactory, Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String, Value> valueMap = new LinkedHashMap<String, Value>();

	public ResultSetPropertyFactory(ResultSet resultSet) throws SQLException {
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			String tableName = getTableName(metaData, i);
			String name = SqlUtils.lookupColumnName(metaData, i);
			String key = StringUtils.isEmpty(tableName) ? name : (tableName + "." + name);
			Object value = resultSet.getObject(i);
			valueMap.put(key, new AnyValue(value));
		}
	}

	protected String getTableName(ResultSetMetaData metaData, int i) throws SQLException {
		return metaData.getTableName(i);
	}

	@Override
	public Value getValue(String key) {
		return valueMap.get(key);
	}

	@Override
	public boolean containsKey(String key) {
		return valueMap.containsKey(key);
	}

	@Override
	public Iterator<String> iterator() {
		return valueMap.keySet().iterator();
	}
}
