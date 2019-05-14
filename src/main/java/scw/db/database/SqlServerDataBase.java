package scw.db.database;

import scw.core.QueryString;
import scw.core.exception.NotFoundException;
import scw.core.utils.StringUtils;

/**
 * Sql Server7.0/2000数据库
 * 
 * @author shuchaowen
 *
 */
public class SqlServerDataBase extends AbstractDataBase {
	private String database;
	private String connectionUrl;
	private final String driverClass;

	public SqlServerDataBase(String driverClass, String url, String username, String password) {
		super(username, password, DataBaseType.SqlServer);
		this.driverClass = driverClass;
		
		int databaseBeginIndex = url.indexOf("//");
		if (databaseBeginIndex == -1) {
			throw new NotFoundException("无法解析数据库名称：" + url);
		}

		databaseBeginIndex = url.indexOf(";", databaseBeginIndex + 2);
		if (databaseBeginIndex == -1) {
			throw new NotFoundException("无法解析数据库名称：" + url);
		}

		String query = url.substring(databaseBeginIndex + 1);
		QueryString queryString = new QueryString(query);
		this.database = queryString.getFirst("DatabaseName");
		if (StringUtils.isEmpty(this.database)) {
			throw new NotFoundException("无法解析数据库名称：" + url);
		}

		this.connectionUrl = url.substring(0, databaseBeginIndex - 1);
	}

	public String getDataBase() {
		return database;
	}

	public String getConnectionURL() {
		return connectionUrl;
	}

	public String getDriverClassName() {
		return StringUtils.isEmpty(driverClass) ? getDataBaseType().getDriverClassName() : driverClass;
	}
}
