package io.basc.framework.mysql;

import java.net.URI;
import java.net.URISyntaxException;

import io.basc.framework.jdbc.template.DatabaseURL;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

public class MysqlURL implements DatabaseURL {
	private static final String PROTOCOL = "jdbc:mysql";
	private final String host;
	private int port = -1;
	private String databaseName;
	private final String query;

	public MysqlURL(String url) {
		Assert.isTrue(url.toLowerCase().startsWith(PROTOCOL + ":"), "Wrong URL " + url);
		String rawUrl = url.substring(PROTOCOL.length() + 1);
		// 借用uri的规则来解析
		URI uri;
		try {
			uri = new URI(rawUrl);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Wrong URL " + url);
		}

		this.host = uri.getHost();
		this.port = uri.getPort();
		this.query = uri.getQuery();
		this.databaseName = StringUtils.cleanPath(uri.getPath());
		if (this.databaseName.startsWith("/")) {
			this.databaseName = databaseName.substring(1);
		}
	}

	private MysqlURL(String host, int port, String databaseName, String query) {
		this.host = host;
		this.port = port;
		this.databaseName = databaseName;
		this.query = query;
	}

	@Override
	public String getProtocol() {
		return PROTOCOL;
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public int getProt() {
		return port;
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
		return query;
	}

	@Override
	public String getRawURL() {
		StringBuilder sb = new StringBuilder();
		sb.append(PROTOCOL).append("://");
		sb.append(host);
		if (port >= 0) {
			sb.append(":").append(port);
		}

		if (StringUtils.isNotEmpty(databaseName)) {
			sb.append("/").append(databaseName);
		}

		if (StringUtils.isNotEmpty(query)) {
			sb.append("?").append(query);
		}
		return sb.toString();
	}

	@Override
	public final String toString() {
		return getRawURL();
	}

	public DatabaseURL clone() {
		return new MysqlURL(host, port, databaseName, query);
	}
}
