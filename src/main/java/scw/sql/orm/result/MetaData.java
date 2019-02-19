package scw.sql.orm.result;

import java.io.Serializable;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.common.exception.AlreadyExistsException;

public final class MetaData implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 如果查询结果中未出现重名
	 */
	private HashMap<String, Integer> asSingleIndexMap;
	private boolean asSingle;// 查询结果中是否存在重复的名字

	private HashMap<String, Map<String, Integer>> metaData;;
	private MetaDataColumn[] metaDataColumns;

	protected MetaData() {
	};

	public MetaData(ResultSetMetaData resultSetMetaData) throws SQLException {
		metaData = new HashMap<String, Map<String, Integer>>(4);
		metaDataColumns = new MetaDataColumn[resultSetMetaData.getColumnCount()];
		asSingleIndexMap = new HashMap<String, Integer>();
		for (int i = 0; i < metaDataColumns.length; i++) {
			MetaDataColumn metaDataColumn = new MetaDataColumn(resultSetMetaData, i + 1);
			metaDataColumns[i] = metaDataColumn;

			if (!asSingle) {
				if (asSingleIndexMap.containsKey(metaDataColumn.getLabelName())) {
					asSingleIndexMap = null;
					asSingle = true;
				} else {
					asSingleIndexMap.put(metaDataColumn.getLabelName(), i);
				}
			}

			Map<String, Integer> map = metaData.get(metaDataColumn.getTableName());
			if (map == null) {
				map = new HashMap<String, Integer>();
				metaData.put(metaDataColumn.getTableName(), map);
			}

			if (map.containsKey(metaDataColumn.getLabelName())) {
				throw new AlreadyExistsException(
						metaDataColumn.getTableName() + " field name [" + metaDataColumn.getLabelName() + "]");
			}
			map.put(metaDataColumn.getLabelName(), i);
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

	public int getSingleIndex(String column) {
		Integer index = asSingleIndexMap.get(column);
		return index == null ? -1 : index;
	}

	public boolean isAsSingle() {
		return asSingle;
	}

	public void setAsSingle(boolean asSingle) {
		this.asSingle = asSingle;
	}

	public boolean isEmpty() {
		return metaDataColumns == null || metaDataColumns.length == 0;
	}
}
