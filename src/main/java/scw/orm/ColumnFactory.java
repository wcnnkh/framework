package scw.orm;

import java.util.Map;

public interface ColumnFactory {
	Map<String, Column> getColumnMap(Class<?> clazz);
}
