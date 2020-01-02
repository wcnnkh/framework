package scw.orm;

import java.util.Map;

public interface ColumnFactory {
	Map<String, ? extends Column> getColumnMap(Class<?> clazz);
}
