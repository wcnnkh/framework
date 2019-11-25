package scw.sql.orm;

public interface TableInfoFactory {
	TableInfo getTableInfo(Class<?> tableClass);
}
