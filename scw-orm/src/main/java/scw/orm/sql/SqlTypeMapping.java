package scw.orm.sql;

public interface SqlTypeMapping {
	SqlType getSqlType(Class<?> javaType);
}
