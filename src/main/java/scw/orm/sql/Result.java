package scw.orm.sql;

import java.io.Serializable;
import java.util.Map;

import scw.orm.MappingOperations;

public interface Result extends Serializable {
	public static final EmptyResult EMPTY_RESULT = new EmptyResult();

	<T> T get(MappingOperations mappingOperations, Class<T> clazz, TableNameMapping tableNameMapping);

	<T> T get(SqlMappingOperations mappingOperations, Class<T> clazz, String tableName);

	<T> T get(SqlMappingOperations mappingOperations, Class<T> clazz);

	Map<String, Object> getTableValueMap(String tableName);

	Object[] getValues();

	<T> T get(Class<T> type, int index);

	<T> T get(int index);

	int size();

	boolean isEmpty();
}
