package scw.orm.sql;

/**
 * 类与表名的映射
 * @author shuchaowen
 *
 */
public interface TableNameFactory {
	String getTableName(Class<?> clazz);
}
