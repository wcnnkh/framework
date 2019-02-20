package scw.db;

public interface MaxIdByDB {
	
	<T> T getMaxValue(Class<T> type, Class<?> tableClass, String tableName, String columnName);

	<T> T getMaxValue(Class<T> type, Class<?> tableClass, String columnName);

	int getMaxIntValue(Class<?> tableClass, String fieldName);

	long getMaxLongValue(Class<?> tableClass, String fieldName);
}
