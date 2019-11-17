package scw.db.support;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;

import scw.core.Destroy;
import scw.db.DBUtils;
import scw.db.database.DataBase;

import com.alibaba.druid.pool.DruidDataSource;

public abstract class AbstractDruidDBConfig extends AbstractDBConfig implements Destroy {
	private DruidDataSource datasource;
	private DataBase dataBase;
	
	static{
		DruidDataSource.class.getName();
	}

	@SuppressWarnings("rawtypes")
	public AbstractDruidDBConfig(Map properties) {
		super(properties);
		datasource = new DruidDataSource();
		DBUtils.loadProperties(datasource, properties);
		if (!datasource.isPoolPreparedStatements()) {// 如果配置文件中没有开启psCache
			datasource.setMaxPoolPreparedStatementPerConnectionSize(20);
		}

		datasource.setRemoveAbandoned(false);
		this.dataBase = DBUtils.automaticRecognition(
				datasource.getDriverClassName(), datasource.getUrl(),
				datasource.getUsername(), datasource.getPassword());
		dataBase.create();
	}

	public DataBase getDataBase() {
		return dataBase;
	}

	public Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}

	public void destroy() {
		datasource.close();
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			try {
				DriverManager.deregisterDriver(drivers.nextElement());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
