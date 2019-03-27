package scw.db.database;

import scw.sql.orm.SqlFormat;
import scw.sql.orm.mysql.MysqlFormat;

/**
 * 常见数据库
 * 
 * @author shuchaowen
 *
 */
public enum DataBaseType {
	Oracle("oracle.jdbc.driver.OracleDriver", null), 
	DB2("com.ibm.db2.jdbc.app.DB2Driver", null),
	SqlServer("com.microsoft.jdbc.sqlserver.SQLServerDriver", null),
	Sybase("com.sybase.jdbc.SybDriver", null),
	Informix("com.informix.jdbc.IfxDriver", null), 
	MySQL("com.mysql.jdbc.Driver", new MysqlFormat()),
	PostgreSQL("org.postgresql.Driver", null), 
	access("sun.jdbc.odbc.JdbcOdbcDriver", null), 
	Teradata("com.ncr.teradata.TeraDriver", null), 
	Netezza("org.netezza.Driver", null),
	SQLite("org.sqlite.JDBC", null)
	;

	private final String driverClassName;
	private final SqlFormat sqlFormat;

	/**
	 * @param driverClass 默认的数据库驱动
	 * @param sqlFormat 对象转sql的格式化方式
	 * @param dataBaseURL url解析方式
	 */
	private DataBaseType(String driverClassName, SqlFormat sqlFormat) {
		this.driverClassName = driverClassName;
		this.sqlFormat = sqlFormat;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public SqlFormat getSqlFormat() {
		return sqlFormat;
	}
}
