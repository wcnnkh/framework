package io.basc.framework.jdbc.template.support;

import java.sql.Connection;
import java.sql.SQLException;

import io.basc.framework.jdbc.support.ConnectionInfo;
import io.basc.framework.jdbc.support.SimpleConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseDialect;
import io.basc.framework.jdbc.template.DatabaseURL;
import io.basc.framework.lang.UnsupportedException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SimpleDatabaseConnectionFactory2 extends DefaultDatabaseConnectionFactory<SimpleConnectionFactory> {

	public SimpleDatabaseConnectionFactory2(SimpleConnectionFactory connectionFactory,
			DatabaseDialect databaseDialect) {
		super(connectionFactory, databaseDialect);
		if (databaseDialect != null) {
			setDatabaseURL(databaseDialect.resolveUrl(connectionFactory.getUrl()));
		}
	}

	public SimpleDatabaseConnectionFactory2(String url, ConnectionInfo info, DatabaseDialect databaseDialect) {
		this(new SimpleConnectionFactory(url, info), databaseDialect);
	}

	public SimpleDatabaseConnectionFactory2(String url, DatabaseDialect databaseDialect) {
		this(url, null, databaseDialect);
	}

	public Connection getConnection() throws SQLException {
		// TODO 判断一下数据库是否存在,如果不存在就创建
		return super.getConnection();
	}

	@Override
	public DatabaseConnectionFactory getDatabaseConnectionFactory(String databaseName) throws UnsupportedException {
		return getDatabaseConnectionFactory(databaseName, () -> {
			DatabaseURL databaseURL = getDatabaseURL();
			if (databaseURL == null) {
				return null;
			}

			databaseURL = databaseURL.clone();
			databaseURL.setDatabaseName(databaseName);
			return new SimpleConnectionFactory(databaseURL.getRawURL(), getConnectionFactory().getInfo());
		}, (e) -> new SimpleDatabaseConnectionFactory2(e, getDatabaseDialect()));
	}
}
