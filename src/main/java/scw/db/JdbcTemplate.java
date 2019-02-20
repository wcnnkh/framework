package scw.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import scw.db.sql.MysqlSelect;
import scw.db.sql.Select;
import scw.memcached.Memcached;
import scw.redis.Redis;
import scw.sql.ConnectionFactory;
import scw.sql.DataSourceConnectionFactory;
import scw.sql.orm.SqlFormat;
import scw.sql.orm.cache.AbstractORMCacheTemplate;
import scw.sql.orm.cache.Cache;
import scw.sql.orm.cache.TransactionCache;
import scw.transaction.sql.SqlTransactionUtils;

public class JdbcTemplate extends AbstractORMCacheTemplate implements MaxIdByDB {
	private final ConnectionFactory connectionFactory;

	public JdbcTemplate(DataSource dataSource, SqlFormat sqlFormat) {
		this(dataSource, sqlFormat, null);
	}

	public JdbcTemplate(ConnectionFactory connectionFactory, SqlFormat sqlFormat) {
		this(connectionFactory, sqlFormat, null);
	}

	public JdbcTemplate(DataSource dataSource, SqlFormat sqlFormat, Memcached memcached, int exp) {
		this(dataSource, sqlFormat, new TransactionCache(memcached, exp));
	}

	public JdbcTemplate(ConnectionFactory connectionFactory, SqlFormat sqlFormat, Memcached memcached, int exp) {
		this(connectionFactory, sqlFormat, new TransactionCache(memcached, exp));
	}

	public JdbcTemplate(DataSource dataSource, SqlFormat sqlFormat, Redis redis, int exp) {
		this(dataSource, sqlFormat, new TransactionCache(redis, exp));
	}

	public JdbcTemplate(ConnectionFactory connectionFactory, SqlFormat sqlFormat, Redis redis, int exp) {
		this(connectionFactory, sqlFormat, new TransactionCache(redis, exp));
	}

	public JdbcTemplate(DataSource dataSource, SqlFormat sqlFormat, Cache cache) {
		super(sqlFormat, cache);
		this.connectionFactory = new DataSourceConnectionFactory(dataSource);
	}

	public JdbcTemplate(ConnectionFactory connectionFactory, SqlFormat sqlFormat, Cache cache) {
		super(sqlFormat, cache);
		this.connectionFactory = connectionFactory;
	}

	public Connection getConnection() throws SQLException {
		return SqlTransactionUtils.getCurrentConnection(connectionFactory);
	}

	public Select createSelect() {
		return new MysqlSelect(this);
	}

	public <T> T getMaxValue(Class<T> type, Class<?> tableClass, String tableName, String columnName) {
		Select select = createSelect();
		select.desc(tableClass, columnName);
		return select.getResultSet().getFirst().get(type, tableName);
	}

	public <T> T getMaxValue(Class<T> type, Class<?> tableClass, String columnName) {
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
