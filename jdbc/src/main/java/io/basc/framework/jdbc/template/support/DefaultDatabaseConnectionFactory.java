package io.basc.framework.jdbc.template.support;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import io.basc.framework.jdbc.ConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseDialect;
import io.basc.framework.jdbc.template.DatabaseURL;
import io.basc.framework.lang.Nullable;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.function.Processor;
import io.basc.framework.util.function.Source;

public class DefaultDatabaseConnectionFactory<F extends ConnectionFactory> implements DatabaseConnectionFactory {
	private final F connectionFactory;
	@Nullable
	private final DatabaseDialect databaseDialect;
	private volatile DatabaseURL databaseURL;
	private volatile String databaseName;
	private volatile Map<String, DefaultDatabaseConnectionFactory<F>> connectionFactoryMap;

	public DefaultDatabaseConnectionFactory(F connectionFactory, @Nullable DatabaseDialect databaseDialect) {
		Assert.requiredArgument(connectionFactory != null, "connectionFactory");
		this.connectionFactory = connectionFactory;
		this.databaseDialect = databaseDialect;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return connectionFactory.getConnection();
	}

	public F getConnectionFactory() {
		return connectionFactory;
	}

	@Override
	public DatabaseConnectionFactory getDatabaseConnectionFactory(String databaseName) throws UnsupportedException {
		return getDatabaseConnectionFactory(databaseName, null,
				(e) -> new DefaultDatabaseConnectionFactory<>(e, this.databaseDialect));
	}

	protected <E extends Throwable> DefaultDatabaseConnectionFactory<F> getDatabaseConnectionFactory(
			String databaseName, @Nullable Source<? extends F, ? extends E> defaultSource,
			Processor<? super F, ? extends DefaultDatabaseConnectionFactory<F>, ? extends E> creator) throws E {
		if (StringUtils.equals(getDatabaseName(), databaseName)) {
			return this;
		}

		synchronized (connectionFactoryMap == null ? this : connectionFactory) {
			DefaultDatabaseConnectionFactory<F> databaseConnectionFactory = connectionFactoryMap == null ? null
					: connectionFactoryMap.get(databaseName);
			if (databaseConnectionFactory != null) {
				return databaseConnectionFactory;
			}

			if (defaultSource == null) {
				return null;
			}

			F connectionFactory = defaultSource.get();
			if (connectionFactory == null) {
				return null;
			}

			connectionFactoryMap = new HashMap<>();
			databaseConnectionFactory = creator.process(connectionFactory);
			connectionFactoryMap.put(databaseName, databaseConnectionFactory);

			// 保存自身
			String selfDatbaseName = getDatabaseName();
			if (selfDatbaseName != null) {
				connectionFactoryMap.put(selfDatbaseName, this);
			}
			databaseConnectionFactory.setDatabaseName(databaseName);
			// 共用同一个map
			databaseConnectionFactory.connectionFactoryMap = this.connectionFactoryMap;

			// 插入url
			DatabaseURL databaseURL = getDatabaseURL();
			if (databaseURL != null) {
				databaseURL = databaseURL.clone();
				databaseURL.setDatabaseName(databaseName);
				databaseConnectionFactory.setDatabaseURL(databaseURL);
			}
			return databaseConnectionFactory;
		}
	}

	@Override
	public DatabaseDialect getDatabaseDialect() {
		return databaseDialect;
	}

	@Override
	public String getDatabaseName() {
		if (databaseName == null) {
			synchronized (this) {
				if (databaseName == null) {
					DatabaseURL databaseURL = getDatabaseURL();
					if (databaseURL != null) {
						this.databaseName = databaseURL.getDatabaseNmae();
					}

					if (StringUtils.isEmpty(databaseName) && databaseDialect != null) {
						this.databaseName = databaseDialect.getSelectedDatabaseName(connectionFactory.operations());
					}
				}
			}
		}
		return databaseName;
	}

	@Override
	public Elements<String> getDatabaseNames() {
		Set<String> names = new LinkedHashSet<>();
		String databaseName = getDatabaseName();
		if (databaseName != null) {
			names.add(databaseName);
		}
		if (this.connectionFactoryMap != null) {
			synchronized (this) {
				if (this.connectionFactoryMap != null) {
					names.addAll(connectionFactoryMap.keySet());
				}
			}
		}
		return Elements.of(names).concat(databaseDialect.getDatabaseNames(operations())).distinct();
	}

	public DatabaseURL getDatabaseURL() {
		return databaseURL;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public void setDatabaseURL(DatabaseURL databaseURL) {
		this.databaseURL = databaseURL;
	}
}
