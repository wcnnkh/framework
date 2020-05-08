package scw.sql.orm;

/**
 * 表名解析器
 * @author shuchaowen
 *
 */
public interface TableNameResolver {
	String getTableName(Class<?> clazz);
}
