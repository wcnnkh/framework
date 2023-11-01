package io.basc.framework.jdbc.template.support;

import java.sql.Connection;
import java.sql.SQLException;

import io.basc.framework.jdbc.ConnectionFactory;
import io.basc.framework.jdbc.support.ConnectionInfo;
import io.basc.framework.jdbc.support.DefaultConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseDialect;
import io.basc.framework.jdbc.template.DatabaseURL;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DefaultDatabaseConnectionFactory extends DefaultConnectionFactory implements DatabaseConnectionFactory {
	private final DatabaseDialect databaseDialect;
	private volatile DatabaseURL databaseURL;
	private volatile String databaseName;
	private volatile ConnectionFactory serverConnectionFactory;

	public DefaultDatabaseConnectionFactory(String url, DatabaseDialect databaseDialect) {
		this(url, null, databaseDialect);
	}

	public DefaultDatabaseConnectionFactory(String url, ConnectionInfo info, DatabaseDialect databaseDialect) {
		super(url, info);
		this.databaseDialect = databaseDialect;
	}

	public DatabaseURL getDatabaseURL() {
		if (this.databaseURL == null) {
			synchronized (this) {
				if (this.databaseURL == null) {
					this.databaseURL = this.databaseDialect.resolveUrl(getUrl());
				}
			}
		}
		return this.databaseURL;
	}

	@Override
	public String getDatabaseName() {
		if (this.databaseName == null) {
			synchronized (this) {
				if (this.databaseName == null) {
					this.databaseName = this.getDatabaseURL().getDatabaseNmae();
				}
			}
		}
		return this.databaseName;
	}

	@Override
	public Elements<String> getDatabaseNames() {
		if (this.serverConnectionFactory != null) {
			return databaseDialect.getDatabaseNames(serverConnectionFactory.operations());
		} else {
			return databaseDialect.getDatabaseNames(operations());
		}
	}

	@Override
	public DatabaseConnectionFactory newDatabase(String databaseName) throws UnsupportedException {
		DatabaseURL databaseURL = getDatabaseURL().clone();
		databaseURL.setDatabaseName(databaseName);
		DefaultDatabaseConnectionFactory connectionFactory = new DefaultDatabaseConnectionFactory(
				databaseURL.toString(), getInfo(), this.databaseDialect);
		connectionFactory.setDatabaseName(databaseName);
		connectionFactory.setDatabaseURL(databaseURL);
		connectionFactory.setServerConnectionFactory(this.serverConnectionFactory);
		return connectionFactory;
	}

	@Override
	public Connection getConnection() throws SQLException {
		// TODO 判断一下数据库是否存在,如果不存在就创建
		return super.getConnection();
	}
}
