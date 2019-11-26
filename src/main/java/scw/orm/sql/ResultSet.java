package scw.orm.sql;

import java.io.Serializable;
import java.util.List;

import scw.orm.MappingOperations;

public interface ResultSet extends Serializable, Iterable<Result> {
	/**
	 * 返回全部数据
	 * 
	 * @return
	 */
	List<Object[]> getList();

	<T> List<T> getList(MappingOperations mappingOperations, Class<T> clazz, TableNameMapping tableNameMapping);

	<T> List<T> getList(SqlMappingOperations mappingOperations, Class<T> clazz, String tableName);

	<T> List<T> getList(SqlMappingOperations mappingOperations, Class<T> clazz);

	int size();

	/**
	 * 获取第一个
	 * 
	 * @return
	 */
	Result getFirst();

	/**
	 * 获取第最后一个
	 * 
	 * @return
	 */
	Result getLast();

	boolean isEmpty();
}