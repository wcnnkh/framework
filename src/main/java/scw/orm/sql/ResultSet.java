package scw.orm.sql;

import java.io.Serializable;
import java.util.List;

public interface ResultSet extends Serializable, Iterable<Result> {
	public static final EmptyResultSet EMPTY_RESULT_SET = new EmptyResultSet();
	/**
	 * 返回全部数据
	 * 
	 * @return
	 */
	List<Object[]> getList();

	<T> List<T> getList(SqlMappingOperations mappingOperations, Class<T> clazz, TableNameMapping tableNameMapping);

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