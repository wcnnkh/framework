package scw.orm.sql.convert;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.value.AnyValue;
import scw.value.PropertyFactory;
import scw.value.Value;

public class ResultSetPropertyFactory implements PropertyFactory {
	private Map<String, Value> valueMap = new LinkedHashMap<String, Value>();

	public void addResultSet(ResultSet resultSet) throws SQLException {
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			String tableName = metaData.getTableName(i);
			String name = metaData.getColumnLabel(i);
			String key = tableName + "." + name;
			Object value = resultSet.getObject(i);
			valueMap.put(key, new AnyValue(value));
		}
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
