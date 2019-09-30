package scw.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import scw.core.utils.PropertiesUtils;
import scw.core.utils.StringUtils;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.db.async.AsyncInfo;
import scw.db.cache.LazyCacheManager;
import scw.db.database.DataBase;
import scw.mq.MQ;

/**
 * 只在能java8中使用 除非你在pom引入你需要的版本，并排除本项目自带的版本
 * 
 * @author shuchaowen
 *
 */

@SuppressWarnings("rawtypes")
public class HikariCPDB extends LazyCacheDB{
	private HikariDataSource hds;
	private DataBase dataBase;

	@Override
	public DataBase getDataBase() {
		return dataBase;
	}

	private void init(Map properties) {
		HikariConfig config = new HikariConfig();
		DBUtils.loadProperties(config, properties);

		this.dataBase = DBUtils.automaticRecognition(
				config.getDriverClassName(), config.getJdbcUrl(),
				config.getUsername(), config.getPassword());
		setDebug(StringUtils.parseBoolean(properties.get("debug")));
		
		dataBase.create();
		hds = new HikariDataSource(config);
		
		Object createTable = properties.get("create");
		if(createTable != null){
			String create = createTable.toString();
			if(!StringUtils.isEmpty(create)){
				createTable(create);
			}
		}
	}

	public HikariCPDB(LazyCacheManager lazyCacheManager, MQ<AsyncInfo> mq,
			String queueName, Map properties) {
		super(lazyCacheManager, mq, queueName);
		init(properties);
	}

	public HikariCPDB(Memcached memcached, String cacheKeyPrefix,
			String queueName, Map properties) {
		super(memcached, cacheKeyPrefix, queueName);
		init(properties);
	}

	public HikariCPDB(Memcached memcached, String cacheKeyPrefix, Map properties) {
		this(memcached, cacheKeyPrefix, null, properties);
	}

	public HikariCPDB(Memcached memcached, Map properties) {
		this(memcached, null, null, properties);
	}
	
	public HikariCPDB(Memcached memcached, String propertiesFile) {
		this(memcached, null, null, PropertiesUtils.getProperties(propertiesFile));
	}

	public HikariCPDB(Redis redis, String cacheKeyPrefix, String queueName,
			Map properties) {
		super(redis, cacheKeyPrefix, queueName);
		init(properties);
	}

	public HikariCPDB(Redis redis, String cacheKeyPrefix, Map properties) {
		super(redis, cacheKeyPrefix, null);
		init(properties);
	}

	public HikariCPDB(Redis redis, Map properties) {
		this(redis, null, properties);
	}
	
	public HikariCPDB(Redis redis, String propertiesFile) {
		this(redis, null, PropertiesUtils.getProperties(propertiesFile));
	}

	public HikariCPDB(Map properties) {
		super();
		init(properties);
	}
	
	public HikariCPDB(String propertiesFile) {
		super();
		init(PropertiesUtils.getProperties(propertiesFile));
	}

	public void createDataBase() {
		if (dataBase != null) {
			dataBase.create();
		}
	}

	public Connection getConnection() throws SQLException {
		return hds.getConnection();
	}

	public void destroy() {
		hds.close();
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			try {
				DriverManager.deregisterDriver(drivers.nextElement());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		super.destroy();
	}

}
