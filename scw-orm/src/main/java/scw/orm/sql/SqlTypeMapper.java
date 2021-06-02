package scw.orm.sql;

/**
 * 获取java类型和数据库类型的映射
 * 
 * @author shuchaowen
 *
 */
public interface SqlTypeMapper {
	SqlType getSqlType(Class<?> javaType);
}
