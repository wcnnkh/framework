package scw.sql.orm;

import java.lang.reflect.Field;

public interface ColumnConvert {
	Object toSqlField(Field field, Object value) throws Exception;

	Object getter(Field field, Object bean) throws Exception;

	void setter(Field field, Object bean, Object value) throws Exception;
}
