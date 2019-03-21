package scw.transaction.cache;

import java.util.List;

import scw.sql.ResultSetMapper;
import scw.sql.RowMapper;
import scw.sql.Sql;
import scw.sql.SqlOperations;
import scw.transaction.Transaction;
import scw.transaction.TransactionManager;

/**
 * 事务内的查询缓存
 * @author shuchaowen
 *
 */
public final class QueryCacheUtils {

	public static boolean queryCacheEnable() {
		Transaction transaction = TransactionManager.getCurrentTransaction();
		if (transaction == null) {
			return false;
		}

		MultipleConnectionQueryCache cache = (MultipleConnectionQueryCache) transaction
				.getResource(MultipleConnectionQueryCache.class);
		if (cache == null) {
			return false;
		}

		return cache.isEnable();
	}

	public static void setQueryCacheEnable(boolean enable) {
		Transaction transaction = TransactionManager.getCurrentTransaction();
		if (transaction == null) {
			return;
		}

		MultipleConnectionQueryCache cache = (MultipleConnectionQueryCache) transaction
				.getResource(MultipleConnectionQueryCache.class);
		if (cache == null) {
			return;
		}

		cache.setEnable(enable);
	}

	public static <T> T query(SqlOperations sqlOperations, Sql sql, ResultSetMapper<T> resultSetMapper) {
		Transaction transaction = TransactionManager.getCurrentTransaction();
		if (transaction == null) {
			return sqlOperations.query(sql, resultSetMapper);
		}

		MultipleConnectionQueryCache cache = (MultipleConnectionQueryCache) transaction
				.getResource(MultipleConnectionQueryCache.class);
		if (cache == null) {
			return sqlOperations.query(sql, resultSetMapper);
		}

		if (cache.isEnable()) {
			QueryCache queryCache = cache.getQueryCache(sqlOperations);
			return queryCache.query(sql, resultSetMapper);
		} else {
			return sqlOperations.query(sql, resultSetMapper);
		}
	}

	public static <T> List<T> query(SqlOperations sqlOperations, Sql sql, RowMapper<T> rowMapper) {
		Transaction transaction = TransactionManager.getCurrentTransaction();
		if (transaction == null) {
			return sqlOperations.query(sql, rowMapper);
		}

		MultipleConnectionQueryCache cache = (MultipleConnectionQueryCache) transaction
				.getResource(MultipleConnectionQueryCache.class);
		if (cache == null) {
			return sqlOperations.query(sql, rowMapper);
		}

		if (cache.isEnable()) {
			QueryCache queryCache = cache.getQueryCache(sqlOperations);
			return queryCache.query(sql, rowMapper);
		} else {
			return sqlOperations.query(sql, rowMapper);
		}
	}
}
