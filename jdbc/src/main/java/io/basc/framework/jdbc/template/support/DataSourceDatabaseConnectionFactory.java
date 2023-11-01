package io.basc.framework.jdbc.template.support;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import io.basc.framework.jdbc.support.DataSourceConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseDialect;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DataSourceDatabaseConnectionFactory<D extends DataSource> extends DataSourceConnectionFactory<D>
		implements DatabaseConnectionFactory {
	private Map<String, D> datasourceMap;
	private final DatabaseDialect databaseDialect;
	private volatile String databaseName;

	public DataSourceDatabaseConnectionFactory(D dataSource, DatabaseDialect databaseDialect) {
		super(dataSource);
		this.databaseDialect = databaseDialect;
	}

	@Override
	public String getDatabaseName() {
		if (databaseName == null) {
			synchronized (this) {
				if (databaseName == null) {
					databaseName = databaseDialect.getSelectedDatabaseName(operations());
				}
			}
		}
		return databaseName;
	}

	@Override
	public Elements<String> getDatabaseNames() {
		Elements<String> names = databaseDialect.getDatabaseNames(operations());
		if (names.isEmpty() && datasourceMap != null) {
			names = Elements.of(datasourceMap.keySet());
			if (this.databaseName != null) {
				names = names.concat(Elements.singleton(this.databaseName));
			}
		}
		return names;
	}

	public D registerDataSource(String databaseName, D dataSource) {
		synchronized (this) {
			if (datasourceMap == null) {
				datasourceMap = new HashMap<>();
			}
			return datasourceMap.put(databaseName, dataSource);
		}
	}

	public D unregisterDataSource(String databaseName) {
		synchronized (this) {
			if (datasourceMap == null) {
				return null;
			}
			return datasourceMap.remove(databaseName);
		}
	}

	@Override
	public DataSourceDatabaseConnectionFactory<D> newDatabase(String databaseName) throws UnsupportedException {
		if (StringUtils.equals(getDatabaseName(), databaseName)) {
			return this;
		}

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
		throw new UnsupportedException(databaseName);
	}
}
