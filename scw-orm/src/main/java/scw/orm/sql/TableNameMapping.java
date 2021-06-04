package scw.orm.sql;

import java.util.Collection;

public interface TableNameMapping {
	String getTableName(Class<?> clazz);

	Collection<String> getTableNames(Class<?> clazz);
}
