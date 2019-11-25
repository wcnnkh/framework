package scw.orm.sql;

import java.io.Serializable;
import java.util.Map;

import scw.core.Cloneable;
import scw.orm.ORMOperations;

public interface Result extends Serializable, Cloneable {
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
	 * @param ormOperations
	 * @param clazz
	 * @param tableNameFactory
	 * @return
	 */
	<T> T get(ORMOperations ormOperations, Class<T> clazz, TableNameFactory tableNameFactory);

	/**
	 * 在查询结果没有重复列名的情况下，此方法可以满足所有需求
	 * @param ormOperations
	 * @param clazz
	 * @return
	 */
	<T> T get(ORMOperations ormOperations, Class<T> clazz);

	Object[] getValues();

	<T> T get(int index);

	int size();

	boolean isEmpty();
}
