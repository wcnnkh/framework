package scw.orm.sql.dialect;

public interface SqlTypeFactory {
	public static final SqlType VARCHAR = new DefaultSqlType("VARCHAR", 255);
	public static final SqlType BIT = new DefaultSqlType("BIT", 1);
	public static final SqlType TINYINT = new DefaultSqlType("TINYINT", 2);
	public static final SqlType SMALLINT = new DefaultSqlType("SMALLINT", 5);
	public static final SqlType INTEGER = new DefaultSqlType("INTEGER", 10);
	public static final SqlType BIGINT = new DefaultSqlType("BIGINT", 20);
	public static final SqlType FLOAT = new DefaultSqlType("FLOAT", 10);
	public static final SqlType DOUBLE = new DefaultSqlType("DOUBLE", 20);
	public static final SqlType DATE = new DefaultSqlType("DATE", 0);
	public static final SqlType TIMESTAMP = new DefaultSqlType("TIMESTAMP", 0);
	public static final SqlType TIME = new DefaultSqlType("TIME", 0);
	public static final SqlType YEAR = new DefaultSqlType("YEAR", 0);
	public static final SqlType BLOB = new DefaultSqlType("BLOB", 0);
	public static final SqlType CLOB = new DefaultSqlType("CLOB", 0);
	public static final SqlType NUMERIC = new DefaultSqlType("NUMERIC", 0);
	public static final SqlType TEXT = new DefaultSqlType("TEXT", 0);
	
	SqlType getSqlType(Class<?> type);
	
	SqlType getSqlType(String sqlType);
}
