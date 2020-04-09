package scw.orm.sql.support;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.lang.AlreadyExistsException;
import scw.lang.UnsupportedException;
import scw.orm.sql.ValueIndexMapping;

public final class ResultSetValueIndexMapping implements ValueIndexMapping {
	private static final long serialVersionUID = 1L;
	/**
	 * 如果查询结果中未出现重名
	 */
	private HashMap<String, Integer> singleIndexMap;
	private boolean existDuplicateField;// 查询结果中是否存在重复的名字
	private HashMap<String, Map<String, Integer>> indexMap;;
	private int columnCount;

	public ResultSetValueIndexMapping(ResultSetMetaData resultSetMetaData) throws SQLException {
		indexMap = new HashMap<String, Map<String, Integer>>(4);
		singleIndexMap = new HashMap<String, Integer>();
		this.columnCount = resultSetMetaData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			String labelName = resultSetMetaData.getColumnLabel(i);
			if (!existDuplicateField) {
				if (singleIndexMap.containsKey(labelName)) {
					singleIndexMap = null;
					existDuplicateField = true;
				} else {
					singleIndexMap.put(labelName, i - 1);
				}
			}

			String tableName = resultSetMetaData.getTableName(i);
			Map<String, Integer> map = indexMap.get(tableName);
			if (map == null) {
				map = new LinkedHashMap<String, Integer>();
				indexMap.put(tableName, map);
			}

			if (map.containsKey(labelName)) {
				throw new AlreadyExistsException(tableName + " field name [" + labelName + "]");
			}
			map.put(labelName, i - 1);
		}
	}

	public boolean isExistDuplicateField() {
		return existDuplicateField;
	}

	public Map<String, Integer> getSingleIndexMap() {
		return singleIndexMap;
	}

	public Map<String, Integer> getIndexMap(String tableName) {
		return indexMap.get(tableName);
	}

	public int getTableCount() {
		return indexMap.size();
	}

	public int getColumnCount() {
		return columnCount;
	}

	public int getIndex(String tableName, String name) {
		Map<String, Integer> map = indexMap.get(tableName);
		if (map == null) {
			return -1;
		}

		Integer index = map.get(name);
		return index == null ? -1 : index;
	}

	public int getSingleIndex(String name) {
		if (isExistDuplicateField()) {
			throw new UnsupportedException("存在重复的字段，不能使用此方法");
		}

		Integer index = singleIndexMap.get(name);
		return index == null ? -1 : index;
	}
}
