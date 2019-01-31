package scw.database;

import java.util.Arrays;
import java.util.Collection;

import scw.jdbc.Sql;

public class TransactionSql {
	private final ConnectionSource connectionSource;
	private final Collection<Sql> sqls;

	public TransactionSql(ConnectionSource connectionSource, Sql sql) {
		this.sqls = Arrays.asList(sql);
		this.connectionSource = connectionSource;
	}

	public TransactionSql(ConnectionSource connectionSource,
			Collection<Sql> sqls) {
		this.sqls = sqls;
		this.connectionSource = connectionSource;
	}

	public ConnectionSource getConnectionSource() {
		return connectionSource;
	}

	public Collection<Sql> getSqls() {
		return sqls;
	}
}
