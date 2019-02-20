package scw.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import scw.db.sql.MysqlSelect;
import scw.db.sql.Select;
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

public class JdbcTemplate extends AbstractORMCacheTemplate implements MaxIdByDB {
	private final ConnectionFactory connectionFactory;
	private final boolean lazy;// 延迟执行

	public JdbcTemplate(DataSource dataSource, SqlFormat sqlFormat, boolean lazy) {
		this(dataSource, sqlFormat, null, lazy);
	}

	public JdbcTemplate(ConnectionFactory connectionFactory,
			SqlFormat sqlFormat, boolean lazy) {
		this(connectionFactory, sqlFormat, null, lazy);
	}

	public JdbcTemplate(DataSource dataSource, SqlFormat sqlFormat,
			Memcached memcached, int exp, boolean lazy) {
		this(dataSource, sqlFormat, new TransactionCache(memcached, exp), lazy);
	}

	public JdbcTemplate(ConnectionFactory connectionFactory,
			SqlFormat sqlFormat, Memcached memcached, int exp, boolean lazy) {
		this(connectionFactory, sqlFormat,
				new TransactionCache(memcached, exp), lazy);
	}

	public JdbcTemplate(DataSource dataSource, SqlFormat sqlFormat,
			Redis redis, int exp, boolean lazy) {
		this(dataSource, sqlFormat, new TransactionCache(redis, exp), lazy);
	}

	public JdbcTemplate(ConnectionFactory connectionFactory,
			SqlFormat sqlFormat, Redis redis, int exp, boolean lazy) {
		this(connectionFactory, sqlFormat, new TransactionCache(redis, exp),
				lazy);
	}

	public JdbcTemplate(DataSource dataSource, SqlFormat sqlFormat,
			Cache cache, boolean lazy) {
		super(sqlFormat, cache);
		this.connectionFactory = new DataSourceConnectionFactory(dataSource);
		this.lazy = lazy;
	}

	public JdbcTemplate(ConnectionFactory connectionFactory,
			SqlFormat sqlFormat, Cache cache, boolean lazy) {
		super(sqlFormat, cache);
		this.connectionFactory = connectionFactory;
		this.lazy = lazy;
	}

	public Connection getConnection() throws SQLException {
		if (lazy) {
			return connectionFactory.getConnection();
		}
		return SqlTransactionUtils.getCurrentConnection(connectionFactory);
	}

	@Override
	public boolean execute(Sql sql) throws SqlException {
		if (lazy) {
			SqlTransactionUtils.executeSql(connectionFactory, sql);
			return true;
		}
		return super.execute(sql);
	}

	@Override
	public int update(Sql sql) throws SqlException {
		if (lazy) {
			SqlTransactionUtils.executeSql(connectionFactory, sql);
			return 0;
		}
		return super.update(sql);
	}

	public Select createSelect() {
		return new MysqlSelect(this);
	}

	public <T> T getMaxValue(Class<T> type, Class<?> tableClass,
			String tableName, String columnName) {
		Select select = createSelect();
		select.desc(tableClass, columnName);
		return select.getResultSet().getFirst().get(type, tableName);
	}

	public <T> T getMaxValue(Class<T> type, Class<?> tableClass,
			String columnName) {
		return getMaxValue(type, tableClass, null, columnName);
	}

	public int getMaxIntValue(Class<?> tableClass, String fieldName) {
		Integer maxId = getMaxValue(Integer.class, tableClass, fieldName);
		if (maxId == null) {
			maxId = 0;
		}
		return maxId;
	}

	public long getMaxLongValue(Class<?> tableClass, String fieldName) {
		Long maxId = getMaxValue(Long.class, tableClass, fieldName);
		if (maxId == null) {
			maxId = 0L;
		}
		return maxId;
	}
}
