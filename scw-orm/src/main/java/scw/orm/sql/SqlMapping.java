package scw.orm.sql;

public interface SqlMapping {
	SqlType getSqlType(Class<?> javaType);
}
