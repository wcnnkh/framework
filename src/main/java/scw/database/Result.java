package scw.database;

import java.io.Serializable;
import java.util.Map;

public interface Result extends Serializable {
	public final static Result EMPTY_RESULT = new EmptyResult();

	/**
	 * 返回列名和值的HastTable
	 * 
	 * @param tableName
	 * @return
	 */
	Map<String, Object> getValueMap(String tableName);

	/**
	 * 一般用于返回对嵌套类型的处理
	 * 
	 * @param deserializeType
	 * @param type
	 * @param tableMapping
	 * @return
	 */
	<T> T get(Class<T> type, Map<Class<?>, String> tableMapping);

	<T> T get(Class<T> type, String tableName);

	/**
	 * 在查询结果没有重复列名的情况下，此方法可以满足所有需求
	 * @param type
	 * @return
	 */
	<T> T get(Class<T> type);

	Object[] getValues();

	<T> T get(int index);

	int size();

	boolean isEmpty();
}
