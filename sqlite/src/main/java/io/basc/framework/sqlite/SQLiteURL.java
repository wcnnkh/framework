package io.basc.framework.sqlite;

import java.io.File;

import org.sqlite.JDBC;

import io.basc.framework.jdbc.template.DatabaseURL;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

public class SQLiteURL implements DatabaseURL {
	private String host;
	private String databaseName;

	public SQLiteURL(String url) {
		Assert.isTrue(JDBC.isValidURL(url), "Wrong URL " + url);
		String filePath = url.substring(JDBC.PREFIX.length());
		if (StringUtils.isEmpty(filePath)) {
			// 内存数据库
			return;
		}

		filePath = StringUtils.cleanPath(filePath);
		File file = new File(filePath);
		if (file.isFile()) {
			this.host = file.getParent();
			this.databaseName = file.getName();
		} else {
			this.host = file.getPath();
		}
	}

	private SQLiteURL(String host, String databaseName) {
		this.host = host;
		this.databaseName = databaseName;
	}

	@Override
	public String getProtocol() {
		return JDBC.PREFIX.substring(0, JDBC.PREFIX.length() - 2);
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public int getProt() {
		return -1;
	}

	@Override
	public String getDatabaseNmae() {
		return databaseName;
	}

	@Override
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	@Override
	public String getQuery() {
		return null;
	}

	@Override
	public DatabaseURL clone() {
		return new SQLiteURL(this.host, this.databaseName);
	}

	@Override
	public final String toString() {
		return getRawURL();
	}

	@Override
	public String getRawURL() {
		StringBuilder sb = new StringBuilder();
		sb.append(JDBC.PREFIX);
		if (host != null) {
			sb.append(host);
		}

		if (databaseName != null) {
			sb.append(File.separator).append(databaseName);
		}
		return sb.toString();
	}
}
