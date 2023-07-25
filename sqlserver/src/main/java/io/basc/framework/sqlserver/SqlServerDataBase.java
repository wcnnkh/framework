package io.basc.framework.sqlserver;

import io.basc.framework.db.AbstractDataBase;
import io.basc.framework.lang.NotFoundException;
import io.basc.framework.net.uri.UriUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collect.MultiValueMap;

/**
 * Sql Server7.0/2000数据库
 * 
 * @author wcnnkh
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
