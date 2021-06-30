package scw.db.database;

import scw.core.utils.StringUtils;
import scw.lang.NotFoundException;
import scw.mysql.MysqlDialect;

public class MysqlDataBase extends AbstractDataBase {
	private String name;
	private String url;
	private String charsetName = "utf8";
	private String collate = "utf8_general_ci";

	public MysqlDataBase(String driverClass, String url, String username, String password) {
		super(username, password, driverClass, new MysqlDialect());

		int databaseBeginIndex = url.indexOf("//");
		if (databaseBeginIndex == -1) {
			throw new NotFoundException("无法解析数据库名称：" + url);
		}

		databaseBeginIndex = url.indexOf("/", databaseBeginIndex + 2);
		if (databaseBeginIndex == -1) {
			throw new NotFoundException("无法解析数据库名称：" + url);
		}

		databaseBeginIndex++;

		int databaseEndIndex = url.indexOf("?", databaseBeginIndex);
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

	@Override
	public String getCreateSql(String database) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE DATABASE IF NOT EXISTS `").append(database).append("`");
		if (!StringUtils.isEmpty(charsetName)) {
			sb.append(" CHARACTER SET ").append(charsetName);
		}

		if (!StringUtils.isEmpty(collate)) {
			sb.append(" COLLATE ").append(collate);
		}

		return sb.toString();
	}
}
