package scw.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import scw.memcached.Memcached;
import scw.redis.Redis;
import scw.sql.ConnectionFactory;
import scw.sql.Sql;
import scw.sql.SqlException;
import scw.sql.orm.SqlFormat;
import scw.sql.orm.cache.AbstractORMCacheTemplate;
import scw.sql.orm.cache.Cache;
import scw.sql.orm.cache.TransactionCache;
import scw.transaction.sql.SqlTransactionUtils;

public class JdbcTemplate extends AbstractORMCacheTemplate {
	private final ConnectionFactory connectionFactory;
	private final boolean lazy;// 延迟执行

	public JdbcTemplate(DataSource dataSource, SqlFormat sqlFormat, boolean lazy) {
		this(dataSource, sqlFormat, null, lazy);
	}

	public JdbcTemplate(ConnectionFactory connectionFactory, SqlFormat sqlFormat, boolean lazy) {
		this(connectionFactory, sqlFormat, null, lazy);
	}

	public JdbcTemplate(DataSource dataSource, SqlFormat sqlFormat, Memcached memcached, int exp, boolean lazy) {
		this(dataSource, sqlFormat, new TransactionCache(memcached, exp), lazy);
	}

	public JdbcTemplate(ConnectionFactory connectionFactory, SqlFormat sqlFormat, Memcached memcached, int exp,
			boolean lazy) {
		this(connectionFactory, sqlFormat, new TransactionCache(memcached, exp), lazy);
	}

	public JdbcTemplate(DataSource dataSource, SqlFormat sqlFormat, Redis redis, int exp, boolean lazy) {
		this(dataSource, sqlFormat, new TransactionCache(redis, exp), lazy);
	}

	public JdbcTemplate(ConnectionFactory connectionFactory, SqlFormat sqlFormat, Redis redis, int exp, boolean lazy) {
		this(connectionFactory, sqlFormat, new TransactionCache(redis, exp), lazy);
	}

	public JdbcTemplate(DataSource dataSource, SqlFormat sqlFormat, Cache cache, boolean lazy) {
		super(sqlFormat, cache);
		this.connectionFactory = new DataSourceConnectionFactory(dataSource);
		this.lazy = lazy;
	}

	public JdbcTemplate(ConnectionFactory connectionFactory, SqlFormat sqlFormat, Cache cache, boolean lazy) {
		super(sqlFormat, cache);
		this.connectionFactory = connectionFactory;
		this.lazy = lazy;
	}

	public Connection getConnection() throws SQLException {
		if (lazy) {
			return connectionFactory.getConnection();
		}

		return SqlTransactionUtils.getTransactionConnection(connectionFactory);
	}

	@Override
	public boolean execute(Sql sql) throws SqlException {
		if (lazy) {
			boolean b = SqlTransactionUtils.executeSql(connectionFactory, sql);
			if (b) {
				return true;
			}
		}
		return super.execute(sql);
	}

	@Override
	public int update(Sql sql) throws SqlException {
		if (lazy) {
			boolean b = SqlTransactionUtils.executeSql(connectionFactory, sql);
			if (b) {
				return 0;
			}
		}
		return super.update(sql);
	}
}
