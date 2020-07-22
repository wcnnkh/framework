package scw.db.hikaricp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

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
@Configuration(order = Integer.MIN_VALUE)
public class HikariCPDB extends AbstractDB {
	private HikariDataSource hds;
	private DataBase dataBase;

	static {
		HikariConfig.class.getName();
	}

	public HikariCPDB(@ResourceParameter @DefaultValue(DBUtils.DEFAULT_CONFIGURATION) String propertiesFile,
			Redis redis) {
		this(ResourceUtils.getResourceOperations().getFormattedProperties(propertiesFile).getResource(), redis);
	}

	public HikariCPDB(@ResourceParameter @DefaultValue(DBUtils.DEFAULT_CONFIGURATION) String propertiesFile,
			Memcached memcached) {
		this(ResourceUtils.getResourceOperations().getFormattedProperties(propertiesFile).getResource(), memcached);
	}

	public HikariCPDB(@ResourceParameter @DefaultValue(DBUtils.DEFAULT_CONFIGURATION) String propertiesFile) {
		this(ResourceUtils.getResourceOperations().getFormattedProperties(propertiesFile).getResource());
	}

	protected HikariCPDB(Map properties) {
		super(properties);
		initConfig(properties);
	}

	protected HikariCPDB(Map properties, Memcached memcached) {
		super(properties, memcached);
		initConfig(properties);
	}

	protected HikariCPDB(Map properties, Redis redis) {
		super(properties, redis);
		initConfig(properties);
	}

	protected void initConfig(HikariConfig config) {
		this.dataBase = DBUtils.automaticRecognition(config.getDriverClassName(), config.getJdbcUrl(),
				config.getUsername(), config.getPassword());
		dataBase.create();
		hds = new HikariDataSource(config);
	}

	private void initConfig(Map properties) {
		if(properties == null){
			return ;
		}
		HikariConfig config = new HikariConfig();
		DBUtils.loadProperties(config, properties);
		initConfig(config);
		createTableByProperties(properties);
	}

	public DataBase getDataBase() {
		return dataBase;
	}

	public Connection getConnection() throws SQLException {
		return hds.getConnection();
	}

	@Override
	public synchronized void destroy() {
		if (!hds.isClosed()) {
			hds.close();
		}
		super.destroy();
	}

	@Override
	public SqlDialect getSqlDialect() {
		return dataBase.getSqlDialect();
	}

	public void setDataBase(DataBase dataBase) {
		this.dataBase = dataBase;
	}
}
