package io.basc.framework.jdbc.template.support;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import io.basc.framework.jdbc.support.DataSourceConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseDialect;
import io.basc.framework.jdbc.template.DatabaseURL;
import io.basc.framework.lang.Nullable;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.function.Processor;
import io.basc.framework.util.function.Source;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DataSourceDatabaseConnectionFactory<D extends DataSource> extends DataSourceConnectionFactory<D>
		implements DatabaseConnectionFactory {
	private final DatabaseDialect databaseDialect;
	private volatile String databaseName;
	private volatile DatabaseURL databaseURL;
	private volatile Map<String, D> dataSourceMap;

	public DataSourceDatabaseConnectionFactory(D dataSource, DatabaseDialect databaseDialect) {
		super(dataSource);
		this.databaseDialect = databaseDialect;
	}

	@Override
	public String getDatabaseName() {
		return getDatabaseName(true);
	}

	private String getDatabaseName(boolean force) {
		if (databaseName == null) {
			synchronized (this) {
				if (databaseName == null) {
					DatabaseURL databaseURL = getDatabaseURL();
					if (databaseURL != null) {
						this.databaseName = databaseURL.getDatabaseNmae();
					}

					if (force && this.databaseName == null) {
						databaseName = databaseDialect.getSelectedDatabaseName(operations());
					}
				}
			}
		}
		return databaseName;
	}

	@Override
	public Elements<String> getDatabaseNames() {
		Set<String> registerNames = new LinkedHashSet<String>(8);
		String databaseName = getDatabaseName();
		if (databaseName != null) {
			registerNames.add(databaseName);
		}
		if (this.dataSourceMap != null) {
			synchronized (this) {
				if (this.dataSourceMap != null) {
					registerNames.addAll(dataSourceMap.keySet());
				}
			}
		}

		return Elements.of(registerNames).concat(databaseDialect.getDatabaseNames(operations())).distinct();
	}

	public <E extends Throwable> D getDataSource(String databaseName,
			@Nullable Source<? extends D, ? extends E> defaultSource) throws E {
		if (StringUtils.equals(getDatabaseName(), databaseName)) {
			return getDataSource();
		}

		if (dataSourceMap == null && defaultSource != null) {
			synchronized (this) {
				if (dataSourceMap == null && defaultSource != null) {
					D dataSource = defaultSource.get();
					if (dataSource != null) {
						dataSourceMap = new HashMap<String, D>();
						dataSourceMap.put(databaseName, dataSource);
					}
				}
			}
		}
		return dataSourceMap == null ? null : dataSourceMap.get(databaseName);
	}

	public <E extends Throwable, F extends DataSourceDatabaseConnectionFactory<D>> F getDataSourceDatabaseConnectionFactory(
			String databaseName, @Nullable Source<? extends D, ? extends E> defaultSource,
			Processor<? super D, ? extends F, ? extends E> newFactory) throws E {
		D dataSource = getDataSource(databaseName, defaultSource);
		if (dataSource == null) {
			return null;
		}

		F factory = newFactory.process(dataSource);
		factory.setDatabaseName(databaseName);
		DatabaseURL databaseURL = getDatabaseURL();
		if (databaseURL != null) {
			databaseURL = databaseURL.clone();
			databaseURL.setDatabaseName(databaseName);
			factory.setDatabaseURL(databaseURL);
			factory.setDataSourceMap(dataSourceMap);
		}
		return factory;
	}

	@Nullable
	public <E extends Throwable> DataSourceDatabaseConnectionFactory<D> getDataSourceDatabaseConnectionFactory(
			String databaseName) {

		if (datasourceMap != null) {
			D dataSource = datasourceMap.get(databaseName);
			if (dataSource != null) {
				DataSourceDatabaseConnectionFactory<D> connectionFactory = new DataSourceDatabaseConnectionFactory<>(
						dataSource, databaseDialect);
				connectionFactory.setDatabaseName(databaseName);
				connectionFactory.setDatasourceMap(this.datasourceMap);
				return connectionFactory;
			}
		}
		return null;
	}

	@Override
	public DatabaseConnectionFactory newDatabaseConnectionFactory(String databaseName) throws UnsupportedException {
		if (StringUtils.equals(getDatabaseName(), databaseName)) {
			return this;
		}
		
		return getDataSourceDatabaseConnectionFactory(databaseName, null, (e) -> new DataSourceDatabaseConnectionFactory<DataSource>(getDataSource(), databaseDialect))
		
		DataSourceDatabaseConnectionFactory<D> connectionFactory = getDataSourceDatabaseConnectionFactory(databaseName);
		if (connectionFactory == null) {
			throw new UnsupportedException(databaseName);
		}
		
		
		return connectionFactory;
	}
}
