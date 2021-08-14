package scw.sqlserver;

import scw.core.utils.StringUtils;
import scw.db.AbstractDataBase;
import scw.lang.NotFoundException;
import scw.net.uri.UriUtils;
import scw.util.MultiValueMap;

/**
 * Sql Server7.0/2000数据库
 * 
 * @author shuchaowen
 *
 */
public class SqlServerDataBase extends AbstractDataBase {
	private String name;
	private String url;

	public SqlServerDataBase(String driverClass, String url, String username, String password) {
		super(username, password, driverClass);
		int databaseBeginIndex = url.indexOf("//");
		if (databaseBeginIndex == -1) {
			throw new NotFoundException("无法解析数据库名称：" + url);
		}

		databaseBeginIndex = url.indexOf(";", databaseBeginIndex + 2);
		if (databaseBeginIndex == -1) {
			throw new NotFoundException("无法解析数据库名称：" + url);
		}

		String query = url.substring(databaseBeginIndex + 1);
		MultiValueMap<String, String> parameters = UriUtils.getQueryParams(query);
		this.name = parameters.getFirst("DatabaseName");
		if (StringUtils.isEmpty(this.name)) {
			throw new NotFoundException("无法解析数据库名称：" + url);
		}

		this.url = url.substring(0, databaseBeginIndex - 1);
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}
}
