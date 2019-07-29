package scw.transaction.sql.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.sql.ResultSetMapper;
import scw.sql.RowMapper;
import scw.sql.Sql;
import scw.sql.SqlOperations;
import scw.transaction.TransactionException;
import scw.transaction.TransactionResource;
import scw.transaction.savepoint.Savepoint;

final class MultipleConnectionQueryCache implements TransactionResource {
	private Map<SqlOperations, QueryCache> queryCacheMap;
	private boolean enable = QueryCacheUtils.isGlobalCacheEnable();

	public QueryCache getQueryCache(SqlOperations sqlOperations) {
		QueryCache queryCache;
		if (queryCacheMap == null) {
			queryCacheMap = new HashMap<SqlOperations, QueryCache>(4, 1);
			queryCache = new QueryCache(sqlOperations);
			queryCacheMap.put(sqlOperations, queryCache);
		} else {
			queryCache = queryCacheMap.get(sqlOperations);
			if (queryCache == null) {
				queryCache = new QueryCache(sqlOperations);
				queryCacheMap.put(sqlOperations, queryCache);
			}
		}
		return queryCache;
	}

	public <T> List<T> query(SqlOperations sqlOperations, Sql sql, RowMapper<T> rowMapper) {
		if (enable) {
			return getQueryCache(sqlOperations).query(sql, rowMapper);
		} else {
			return sqlOperations.query(sql, rowMapper);
		}
	}

	public <T> T query(SqlOperations sqlOperations, Sql sql, ResultSetMapper<T> resultSetMapper) {
		if (enable) {
			return getQueryCache(sqlOperations).query(sql, resultSetMapper);
		} else {
			return sqlOperations.query(sql, resultSetMapper);
		}
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
		if (!enable) {
			queryCacheMap = null;
		}
	}

	public void process() {
	}

	public void rollback() {
	}

	public void end() {
		queryCacheMap = null;
		this.enable = QueryCacheUtils.isGlobalCacheEnable();
	}

	public Savepoint createSavepoint() throws TransactionException {
		return null;
	}
}
