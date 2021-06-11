package scw.db.database;

import scw.lang.NotFoundException;
import scw.mysql.MysqlDialect;

/**
 * Oracle8/8i/9i数据库（thin模式）
 * 
 * @author shuchaowen
 *
 */
public class OracleDataBase extends AbstractDataBase {
	private String name;
	private String url;

	public OracleDataBase(String driverClass, String url, String username, String password) {
		super(username, password, driverClass, new MysqlDialect());

		int databaseBeginIndex = url.indexOf("@");
		if (databaseBeginIndex == -1) {
			throw new NotFoundException("无法解析数据库名称：" + url);
		}

		databaseBeginIndex = url.indexOf(":", databaseBeginIndex + 1);
		if (databaseBeginIndex == -1) {
			throw new NotFoundException("无法解析数据库名称：" + url);
		}

		databaseBeginIndex = url.indexOf(":", databaseBeginIndex + 1);
		if (databaseBeginIndex == -1) {
			throw new NotFoundException("无法解析数据库名称：" + url);
		}

		databaseBeginIndex++;

		int databaseEndIndex = url.indexOf("/", databaseBeginIndex);
		if (databaseEndIndex == -1) {
			databaseEndIndex = url.indexOf("?", databaseBeginIndex);
		}

		if (databaseEndIndex == -1) {
			databaseEndIndex = url.indexOf("#", databaseBeginIndex);
		}

		if (databaseEndIndex == -1) {
			this.name = url.substring(databaseBeginIndex);
			this.url = url.substring(0, databaseBeginIndex - 1);
		} else {
			this.name = url.substring(databaseBeginIndex, databaseEndIndex);
			this.url = url.substring(0, databaseBeginIndex - 1) + url.substring(databaseEndIndex);
		}
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}
}
