package scw.sql;

import java.util.Arrays;

public class TransactionSql {
	private ConnectionFactory connectionFactory;
	private Iterable<Sql> sqlIterable;

	public TransactionSql(ConnectionFactory connectionFactory, Sql sql) {
		this.connectionFactory = connectionFactory;
		this.sqlIterable = Arrays.asList(sql);
	}

	public TransactionSql(ConnectionFactory connectionFactory, Sql... sqls) {
		this.connectionFactory = connectionFactory;
		this.sqlIterable = Arrays.asList(sqls);
	}

	public TransactionSql(ConnectionFactory connectionFactory, Iterable<Sql> sqlIterable) {
		this.connectionFactory = connectionFactory;
		this.sqlIterable = sqlIterable;
	}

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public Iterable<Sql> getSqlIterable() {
		return sqlIterable;
	}
}
