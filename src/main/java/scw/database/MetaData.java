package scw.database;

import java.io.Serializable;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.common.exception.AlreadyExistsException;

public final class MetaData implements Serializable {
	private static final long serialVersionUID = 1L;
	private HashMap<String, Map<String, Integer>> metaData = new HashMap<String, Map<String, Integer>>(4);
	private MetaDataColumn[] metaDataColumns;

	protected MetaData() {
	};

	public MetaData(ResultSetMetaData resultSetMetaData) throws SQLException {
		metaDataColumns = new MetaDataColumn[resultSetMetaData.getColumnCount()];
		for (int i = 0; i < metaDataColumns.length; i++) {
			MetaDataColumn metaDataColumn = new MetaDataColumn(resultSetMetaData, i + 1);
			metaDataColumns[i] = metaDataColumn;
			Map<String, Integer> map = metaData.get(metaDataColumn.getTableName());
			if (map == null) {
				map = new HashMap<String, Integer>();
				metaData.put(metaDataColumn.getTableName(), map);
			}

			if (map.containsKey(metaDataColumn.getName())) {
				throw new AlreadyExistsException(
						metaDataColumn.getTableName() + " field name [" + metaDataColumn.getName() + "]");
			}
			map.put(metaDataColumn.getName(), i);
		}
	}

	public MetaDataColumn[] getColumns() {
		return metaDataColumns;
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
			return getColumnIndex(columnName, name);
		}
		return -1;
	}

	public int getColumnIndex(String columnName, String... tableNames) {
		if (tableNames == null) {
			return -1;
		}

		for (String name : tableNames) {
			return getColumnIndex(columnName, name);
		}
		return -1;
	}

	public int getColumnIndex(String columnName, String tableName) {
		Map<String, Integer> map = metaData.get(tableName);
		if (map == null) {
			return -1;
		}

		Integer index = map.get(columnName);
		return index == null ? -1 : index;
	}

	public int getDefaultColumnIndex(String column) {
		return getColumnIndex(column, "");
	}

	public boolean isEmpty() {
		return metaDataColumns == null || metaDataColumns.length == 0;
	}
}
