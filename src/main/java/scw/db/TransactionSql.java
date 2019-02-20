package scw.db;

import java.util.Arrays;
import java.util.Collection;

import scw.sql.ConnectionFactory;
import scw.sql.Sql;

public class TransactionSql {
	private final ConnectionFactory connectionSource;
	private final Collection<Sql> sqls;

	public TransactionSql(ConnectionFactory connectionSource, Sql sql) {
		this.sqls = Arrays.asList(sql);
		this.connectionSource = connectionSource;
	}

	public TransactionSql(ConnectionFactory connectionSource,
			Collection<Sql> sqls) {
		this.sqls = sqls;
		this.connectionSource = connectionSource;
	}

	public ConnectionFactory getConnectionSource() {
		return connectionSource;
	}

	public Collection<Sql> getSqls() {
		return sqls;
	}
}
