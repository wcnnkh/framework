package scw.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.alibaba.druid.pool.DruidDataSource;

import scw.db.sql.SQLFormat;

public class DruidDB extends DB{
	private DruidDataSource datasource;
	
	public DruidDB(String url,
			String username, String password, int minSize, int maxSize){
		this(null, url, "com.mysql.jdbc.Driver", username, password, minSize, minSize, maxSize, 20);
	}
	
	public DruidDB(SQLFormat sqlFormat, String url, String driverClass, 
			String username, String password, int initSize, int minSize, int maxSize,
			int maxPoolPreparedStatementPerConnectionSize){
		super(sqlFormat);
		datasource = new DruidDataSource();
		datasource.setUrl(url);
		datasource.setDriverClassName(driverClass);
		datasource.setUsername(username);
		datasource.setPassword(password);
		datasource.setInitialSize(initSize);
		datasource.setMinIdle(minSize);
		datasource.setMaxActive(maxSize);
		datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
	}
	
	public Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}

	public void close() throws Exception {
		datasource.clone();
	}

}
