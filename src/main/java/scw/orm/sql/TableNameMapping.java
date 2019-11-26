package scw.orm.sql;

/**
 * 类与表名的映射
 * @author shuchaowen
 *
 */
public interface TableNameMapping {
	String getTableName(Class<?> clazz);
}
