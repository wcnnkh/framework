package io.basc.framework.db;

/**
 * 常见数据库
 * 
 * @author shuchaowen
 *
 */
public enum DataBaseType {
	Oracle("oracle.jdbc.driver.OracleDriver"), DB2("com.ibm.db2.jdbc.app.DB2Driver"),
	SqlServer("com.microsoft.jdbc.sqlserver.SQLServerDriver"), Sybase("com.sybase.jdbc.SybDriver"),
	Informix("com.informix.jdbc.IfxDriver"), MySQL("com.mysql.jdbc.Driver"), PostgreSQL("org.postgresql.Driver"),
	access("sun.jdbc.odbc.JdbcOdbcDriver"), Teradata("com.ncr.teradata.TeraDriver"), Netezza("org.netezza.Driver"),
	SQLite("org.sqlite.JDBC");

	private final String driverClassName;

	/**
	 * @param driverClass 默认的数据库驱动
	 */
	private DataBaseType(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getDriverClassName() {
		return driverClassName;
	}
}
