package scw.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import scw.beans.annotaion.Destroy;
import scw.common.utils.ConfigUtils;
import scw.sql.orm.SqlFormat;
import scw.sql.orm.cache.Cache;

/**
 * 只在能java8中使用
 * 除非你在pom引入你需要的版本，并排除本项目自带的版本
 * @author shuchaowen
 *
 */
public class HikariCPDB extends DB {
	private HikariDataSource hds;

	public HikariCPDB(SqlFormat sqlFormat, String propertiesFile) {
		this(sqlFormat, null, propertiesFile);
	}

	public HikariCPDB(SqlFormat sqlFormat, Cache cache, String propertiesFile) {
		this(sqlFormat, cache, propertiesFile, "UTF-8");
	}

	public HikariCPDB(SqlFormat sqlFormat, Cache cache, String propertiesFile, String charsetName) {
		super(sqlFormat, cache);
		Properties properties = ConfigUtils.getProperties(propertiesFile, charsetName);
		HikariConfig config = new HikariConfig(properties);
		hds = new HikariDataSource(config);
	}

	public Connection getConnection() throws SQLException {
		return hds.getConnection();
	}

	@Destroy
	public void close() throws Exception {
		hds.close();
	}

}
