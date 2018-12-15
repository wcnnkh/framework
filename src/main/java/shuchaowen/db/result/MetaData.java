package shuchaowen.db.result;

import java.io.Serializable;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MetaData implements Serializable {
	private static final long serialVersionUID = 1L;
	private HashMap<String, Map<String, Integer>> metaData = new HashMap<String, Map<String, Integer>>(
			4);
	private Column[] columns;

	protected MetaData() {
	};

	protected MetaData(ResultSetMetaData resultSetMetaData) throws SQLException {
		columns = new Column[resultSetMetaData.getColumnCount()];
		for (int i = 0; i < columns.length; i++) {
			Column column = new Column(resultSetMetaData.getColumnName(i + 1),
					resultSetMetaData.getTableName(i + 1));
			columns[i] = column;
			Map<String, Integer> map = metaData.get(column.getTableName());
			if (map == null) {
				map = new HashMap<String, Integer>();
				map.put(column.getName(), i);
				metaData.put(column.getTableName(), map);
			} else {
				map.put(column.getName(), i);
			}
		}
	}

	public Map<String, Map<String, Integer>> getMetaData() {
		return metaData;
	}

	public Column[] getColumns() {
		return columns;
	}

	/**
	 * @param tableNameList
	 * @param columnName
	 * @return 不存在返回-1
	 */
	public int getColumnIndex(List<String> tableNameList, String columnName) {
		if (tableNameList == null) {
			return -1;
		}

		for (String name : tableNameList) {
			Map<String, Integer> map = metaData.get(name);
			if (map == null) {
				continue;
			}

			Integer index = map.get(columnName);
			return index == null ? -1 : index;
		}
		return -1;
	}

	public int getColumnIndex(String columnName, String... tableNames) {
		if (tableNames == null) {
			return -1;
		}

		for (String name : tableNames) {
			Map<String, Integer> map = metaData.get(name);
			if (map == null) {
				continue;
			}

			Integer index = map.get(columnName);
			return index == null ? -1 : index;
		}
		return -1;
	}
}
