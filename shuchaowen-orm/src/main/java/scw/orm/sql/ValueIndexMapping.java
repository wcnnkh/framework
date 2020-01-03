package scw.orm.sql;

import java.io.Serializable;
import java.util.Map;

public interface ValueIndexMapping extends Serializable {
	/**
	 * 是否存在重复的字段
	 * @return
	 */
	boolean isExistDuplicateField();

	int getTableCount();

	int getColumnCount();

	Map<String, Integer> getSingleIndexMap();

	Map<String, Integer> getIndexMap(String tableName);

	int getIndex(String tableName, String name);

	int getSingleIndex(String name);
}
