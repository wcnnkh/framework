package scw.db.druid;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.alibaba.druid.pool.DruidDataSource;

import scw.core.instance.annotation.Configuration;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.parameter.annotation.DefaultValue;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.db.AbstractDB;
import scw.db.DBUtils;
import scw.db.database.DataBase;
import scw.io.ResourceUtils;
import scw.sql.orm.dialect.SqlDialect;

@SuppressWarnings("rawtypes")
@Configuration(order = Integer.MIN_VALUE + 1)
public class DruidDB extends AbstractDB {
	private DruidDataSource datasource;
	private DataBase dataBase;

	static {
		DruidDataSource.class.getName();
	}

	public DruidDB(@ResourceParameter @DefaultValue(DBUtils.DEFAULT_CONFIGURATION) String properties) {
		this(ResourceUtils.getResourceOperations().getFormattedProperties(properties).getResource());
	}

	public DruidDB(@ResourceParameter @DefaultValue(DBUtils.DEFAULT_CONFIGURATION) String properties,
			Memcached memcached) {
		this(ResourceUtils.getResourceOperations().getFormattedProperties(properties).getResource(), memcached);
	}

	public DruidDB(@ResourceParameter @DefaultValue(DBUtils.DEFAULT_CONFIGURATION) String properties,
			Redis redis) {
		this(ResourceUtils.getResourceOperations().getFormattedProperties(properties).getResource(), redis);
	}

	protected DruidDB(Map properties) {
		super(properties);
		initConfig(properties);
	}

	protected DruidDB(Map properties, Memcached memcached) {
		super(properties, memcached);
		initConfig(properties);
	}

	protected DruidDB(Map properties, Redis redis) {
		super(properties, redis);
		initConfig(properties);
	}

	private void initConfig(Map properties) {
		if(properties == null){
			return ;
		}
		
		datasource = new DruidDataSource();
		DBUtils.loadProperties(datasource, properties);
		initConfig(datasource);
		createTableByProperties(properties);
	}
	
	protected void initConfig(DruidDataSource dataSource){
		if (!datasource.isPoolPreparedStatements()) {// 如果配置中没有开启psCache
			datasource.setMaxPoolPreparedStatementPerConnectionSize(20);
		}

		datasource.setRemoveAbandoned(false);
		this.dataBase = DBUtils.automaticRecognition(datasource.getDriverClassName(), datasource.getUrl(),
				datasource.getUsername(), datasource.getPassword());
		dataBase.create();
	}

	public DataBase getDataBase() {
		return dataBase;
	}

	public Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}

	@Override
	public SqlDialect getSqlDialect() {
		return dataBase.getSqlDialect();
	}

	public synchronized void destroy() {
		if (datasource != null && !datasource.isClosed()) {
			datasource.close();
		}
		super.destroy();
	}
}
