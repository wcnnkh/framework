package scw.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;

import scw.beans.annotation.Bean;
import scw.core.annotation.Order;
import scw.core.resource.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.db.async.AsyncInfo;
import scw.db.cache.LazyCacheManager;
import scw.db.database.DataBase;
import scw.mq.MQ;

import com.alibaba.druid.pool.DruidDataSource;

@SuppressWarnings("rawtypes")
@Bean(proxy = false)
public class DruidDB extends LazyCacheDB {
	private DruidDataSource datasource;
	private DataBase dataBase;

	@Override
	public DataBase getDataBase() {
		return dataBase;
	}

	public DruidDB(LazyCacheManager lazyCacheManager, MQ<AsyncInfo> mq, String queueName, Map properties) {
		super(lazyCacheManager, mq, queueName);
		init(properties);
	}

	public DruidDB(Memcached memcached, String cacheKeyPrefix, String queueName, Map properties) {
		super(memcached, cacheKeyPrefix, queueName);
		init(properties);
	}

	public DruidDB(Memcached memcached, String cacheKeyPrefix, Map properties) {
		this(memcached, cacheKeyPrefix, null, properties);
	}

	public DruidDB(Memcached memcached, Map properties) {
		this(memcached, null, null, properties);
	}
	
	public DruidDB(Memcached memcached, String propertiesFile) {
		this(memcached, null, null, ResourceUtils.getProperties(propertiesFile));
	}

	public DruidDB(Redis redis, String cacheKeyPrefix, String queueName, Map properties) {
		super(redis, cacheKeyPrefix, queueName);
		init(properties);
	}

	public DruidDB(Redis redis, String cacheKeyPrefix, Map properties) {
		super(redis, cacheKeyPrefix, null);
		init(properties);
	}

	public DruidDB(Redis redis, Map properties) {
		this(redis, null, properties);
	}
	
	public DruidDB(Redis redis, String propertiesFile) {
		this(redis, null, ResourceUtils.getProperties(propertiesFile));
	}

	private void init(Map properties) {
		datasource = new DruidDataSource();
		DBUtils.loadProperties(datasource, properties);
		if (!datasource.isPoolPreparedStatements()) {// 如果配置文件中没有开启psCache
			datasource.setMaxPoolPreparedStatementPerConnectionSize(20);
		}

		datasource.setRemoveAbandoned(false);

		this.dataBase = DBUtils.automaticRecognition(datasource.getDriverClassName(), datasource.getUrl(),
				datasource.getUsername(), datasource.getPassword());

		dataBase.create();
		Object createTable = properties.get("create");
		if(createTable != null){
			String create = createTable.toString();
			if(!StringUtils.isEmpty(create)){
				createTable(create);
			}
		}
	}

	public DruidDB(Map properties) {
		init(properties);
	}
	
	/**
	 * 优先使用此构造方法
	 * @param propertiesFile
	 */
	@Order
	public DruidDB(String propertiesFile) {
		init(ResourceUtils.getProperties(propertiesFile));
	}

	public Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}

	@Override
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
		super.destroy();
	}
}
