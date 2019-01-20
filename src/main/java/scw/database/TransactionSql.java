package scw.database;

import java.util.Arrays;
import java.util.Collection;

public class TransactionSql {
	private final ConnectionSource connectionSource;
	private final Collection<SQL> sqls;

	public TransactionSql(ConnectionSource connectionSource, SQL sql) {
		this.sqls = Arrays.asList(sql);
		this.connectionSource = connectionSource;
	}

	public TransactionSql(ConnectionSource connectionSource,
			Collection<SQL> sqls) {
		this.sqls = sqls;
		this.connectionSource = connectionSource;
	}

	public ConnectionSource getConnectionSource() {
		return connectionSource;
	}

	public Collection<SQL> getSqls() {
		return sqls;
	}
}
